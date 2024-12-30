package guide.app.po;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.tivoli.maximo.report.birt.admin.ReportAdminService;
import com.ibm.tivoli.maximo.report.birt.runtime.ReportParameterData;

import psdi.app.doclink.Docinfo;
import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 *@function:ZEE-PO APPR时,更新模板附件,并发送邮件(updateTemplateDoc流程和Mass emailing按钮公用)
 *@author:zj
 *@date:2024-06-07 16:29:17
 *@modify:
 */
public class UDWFZEESendMailAction implements ActionCustomClass{
	public final static int ownerid = 227; //模板COMMTEMPLATE的COMMTEMPLATEID
	final static String doctype = "Attachments"; //附件文件夹
	
	@Override
	public void applyCustomAction(MboRemote mbo, Object[] arg1)
			throws MXException, RemoteException {
		String udcompany = mbo.getString("udcompany");
	    if (udcompany != null && udcompany.equalsIgnoreCase("ZEE")) {
	    	updateTemplateDoc(mbo); //将PO报表添加到模板
		}
	}
	
	public static void updateTemplateDoc(MboRemote mbo) throws MXException, RemoteException {
		try {
			JSONObject paramRpt = new JSONObject();
			Boolean terms = false;
					
			String[] reportnames = {"udpomatl_djzee.rptdesign","udpomatl_djzeestaticnlpara.rptdesign","udpomatl_djzeestaticenpara.rptdesign"};
			List<String> reportids = Arrays.asList(reportnames);
			
			//清空以前的报表附件	
			MboSetRemote doclinksSet = MXServer.getMXServer().getMboSet("DOCLINKS", MXServer.getMXServer().getSystemUserInfo());
			doclinksSet.setWhere(" ownertable='COMMTEMPLATE' and ownerid='"+ownerid+"' ");
			doclinksSet.reset();
			if (!doclinksSet.isEmpty() && doclinksSet.count() > 0) {
				doclinksSet.deleteAll();
				doclinksSet.save();
			}
			doclinksSet.close();

			MboSetRemote docinfoSet = MXServer.getMXServer().getMboSet("DOCINFO", MXServer.getMXServer().getSystemUserInfo());
			docinfoSet.setWhere(" docinfoid in (select docinfoid from doclinks where ownertable='COMMTEMPLATE' and ownerid='"+ownerid+"') ");
			docinfoSet.reset();
			if (!docinfoSet.isEmpty() && docinfoSet.count() > 0) {
				docinfoSet.deleteAll();
				docinfoSet.save();
			}
			docinfoSet.close();
			
			for(int i = 0; i < reportids.size(); i++){
				paramRpt.put("reportName", reportids.get(i));
				if(reportids.get(i).equalsIgnoreCase("udpomatl_djzee.rptdesign")) {
					paramRpt.put("description", mbo.getString("udorderdept")+mbo.getString("ponum"));
				} else if(reportids.get(i).equalsIgnoreCase("udpomatl_djzeestaticnlpara.rptdesign")) {
					paramRpt.put("description", "StandardTermsAndCondition-NL");
					terms = true;
				} else if(reportids.get(i).equalsIgnoreCase("udpomatl_djzeestaticenpara.rptdesign")) {
					paramRpt.put("description", "StandardTermsAndCondition-EN");
					terms = true;
				}
							
			paramRpt.put("appName", "UDPOZEE");
			paramRpt.put("keyNum", mbo.getString("ponum"));
				
			JSONObject paramData = new JSONObject();
			paramData.put("ponum", mbo.getString("ponum"));
			
			// 报表执行
			String tempFolder = MXServer.getMXServer().getProperty("mxe.doclink.doctypes.defpath")+ File.separator+ "reports\\"+ getString(paramRpt, "description");
			String reportOutputFileName = getReport(MXServer.getMXServer().getSystemUserInfo(), paramRpt, paramData, tempFolder,terms);
			String outPath = tempFolder + "\\" + reportOutputFileName;		
			
			MboSetRemote docinfoYLSet = MXServer.getMXServer().getMboSet("DOCINFO", MXServer.getMXServer().getSystemUserInfo());
			docinfoYLSet.setWhere(" 1=2 ");
			docinfoYLSet.reset();
			
			Docinfo docinfoYL = (Docinfo) docinfoYLSet.add(2L);
			int docinfoYLid = docinfoYL.getInt("docinfoid");
			
			setValueForDocinfo(mbo, docinfoYL, reportOutputFileName, outPath); // 新增docinfo记录

			insertDoclinks(mbo,docinfoYLid); // 新增doclinks记录
			
			docinfoYLSet.close();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static String getReport(UserInfo reportUserInfo, JSONObject paramRpt, JSONObject paramData, String tempFolder,Boolean terms) throws MXException, RemoteException, JSONException {
		String reportOutputFileName = "";
		try {
			// 文件名称
			Date currentDate = MXServer.getMXServer().getDate();
			
//			if(terms == false) {
//				reportOutputFileName = getString(paramRpt, "description") +"-"+ getString(paramRpt, "keyNum") +"-"+ ldateToString(currentDate) + ".pdf";
//			} else {
//				reportOutputFileName = getString(paramRpt, "description")  + ".pdf";
//			}
			reportOutputFileName = getString(paramRpt, "description")  + ".pdf";
			
			String tempfile = tempFolder + File.separator + reportOutputFileName;
			// 创建文件
			File srcfile = new File(tempfile);
			srcfile.getParentFile().mkdirs();

			// 报表参数
			ReportParameterData parameterData = new ReportParameterData();
			Iterator keys = paramData.keys();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				String value = paramData.getString(key);
				parameterData.addParameter(key, value);
			}
			// 报表内容
			ReportAdminService reportAdminService = (ReportAdminService) MXServer.getMXServer().lookup("BIRTREPORT");
			byte reportOutput[] = reportAdminService.runReport(reportUserInfo, getString(paramRpt, "reportName"), getString(paramRpt, "appName"), parameterData, reportOutputFileName, "pdf", null);
			ByteArrayInputStream bis = new ByteArrayInputStream(reportOutput);

			// 报表写入文件
			try {
				createFileFromStream(srcfile, bis);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return reportOutputFileName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reportOutputFileName;
	}
	
	public static String getString(JSONObject js, String attr) throws JSONException {
		String lsrtn = "";
		Object object = js.get(attr);
		if (object != null)
			lsrtn = object.toString();
		return lsrtn;
	}
	
	public static String ldateToString(Date date) {
		String YYYY_MM_DD_HH_MM_SS = "ddMMyyyy-HHmmss";
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
			String time = sdf.format(date);
			return time;
		} else {
			return "";
		}
	}
	
	public static void createFileFromStream(File file, InputStream inputStream) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buf = new byte[1024];
		while (true) {
			int bytesRead = inputStream.read(buf);
			if (bytesRead <= 0) {
				break;
			}
			fos.write(buf, 0, bytesRead);
		}
		fos.flush();
		fos.close();
	}
	
	private static void setValueForDocinfo(MboRemote mbo,MboRemote docinfoYL,String reportOutputFileName,String outPath) throws RemoteException, MXException {
        docinfoYL.setValue("document", "PO-"+mbo.getString("ponum"), 11L);
        docinfoYL.setValue("description", reportOutputFileName, 11L);
        docinfoYL.setValue("createdate", MXServer.getMXServer().getDate(), 11L);
        docinfoYL.setValue("createby", MXServer.getMXServer().getName(), 11L);
        docinfoYL.setValue("changedate", MXServer.getMXServer().getDate(), 11L);
        docinfoYL.setValue("changeby", MXServer.getMXServer().getName(), 11L);
        docinfoYL.setValue("doctype", doctype, 11L);
        docinfoYL.setValue("urltype", "FILE", 11L);
        docinfoYL.setValue("urlname", outPath, 11L);
        docinfoYL.setValue("printthrulinkdflt", "1", 11L);
        docinfoYL.setValue("usedefaultfilepath", "0", 11L);
        docinfoYL.setValue("show", "0", 11L);
        docinfoYL.setValue("langcode", "EN", 11L);
        docinfoYL.getThisMboSet().save();
	}
	
	private static void insertDoclinks(MboRemote mbo,int docinfoYLid) throws RemoteException, MXException {
		try {
			String sql = "insert into doclinks (document,ownertable,ownerid,doctype,getlatestversion,createdate,createby,changedate,changeby,printthrulink,copylinktowo,docinfoid,doclinksid) values ('PO-"+mbo.getString("ponum")+"','COMMTEMPLATE','"+ownerid+"','"+doctype+"','1',sysdate,'"+mbo.getUserInfo().getPersonId()+"',sysdate,'"+mbo.getUserInfo().getPersonId()+"','1','0','"+docinfoYLid+"',doclinksSEQ.NEXTVAL)";
			exeSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void exeSQL(String sql) throws MXException, RemoteException, SQLException {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = MXServer.getMXServer().getDBManager().getConnection(MXServer.getMXServer().getSystemUserInfo().getConnectionKey());

			if (null != connection) {
				stmt = connection.createStatement();
				try {
					stmt.execute(sql);
					connection.commit();
				} catch (SQLException e) {
					connection.rollback();
					System.out.println(sql + e);
				}
			}

		} catch (RemoteException e) {
		} catch (Exception e) {
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e1) {
				}
			}
			try {
				MXServer.getMXServer().getDBManager().freeConnection(MXServer.getMXServer().getSystemUserInfo().getConnectionKey());
				if (connection != null) {
					connection.close();
				}
			} catch (RemoteException e1) {
			} catch (Exception e1) {
			}
		}
	}
	
}
