package guide.iface.datawarehouse;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *@function:ZEE-datawarehouse interface
 *@author:zj
 *@date:2023-10-19 16:27:22
 *@modify:
 */
public class UDZEEDatahouseCrontask extends SimpleCronTask {
	public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	private String isImportLog = "";
	private String isDate = "";
	
	@Override
	public void cronAction() {
		try {
			System.out.println("\n--start-UDZEEDatahouseCrontask--");
			Calendar calendar = Calendar.getInstance();
			Date rt = calendar.getTime();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.isDate = dateFormat.format(rt);
			
			String table = "";
			
			table = "po";
			MboSetRemote poSet = MXServer.getMXServer().getMboSet("PO",this.getRunasUserInfo());
			poSet.setWhere(" udcompany='ZEE' and status='APPR' ");
			poSet.reset();
			System.out.println("\nDATA-PO--"+poSet.count());
			JSONObject jo = new JSONObject();
			if (!poSet.isEmpty() && poSet.count() > 0) {
				String strjo = "";
				for (int i = 0; i < poSet.count(); i++) {
					MboRemote po = poSet.getMbo(i);
					setjo_po(jo,po);
					strjo = strjo + jo.toString();
					if (i!=poSet.count()-1) {
						strjo = strjo + ",";
					}
				}
				String params = "["+strjo+"]";
				String result = postDatahouse(params,table);
				addImportLog(params,result);
				try {
					saveImportLog(table);
				} catch (MXApplicationException e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
		}
	}
	
    public static String postDatahouse(String params,String table){
    	String result = "";
    	try {
            //服务的地址
            URL wsUrl = new URL("https://cspz-data.cspterminals.be/ords/csp/dataload/"+table);

            HttpURLConnection conn = (HttpURLConnection) wsUrl.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            OutputStream os = conn.getOutputStream();

            String soap = params;
            
            os.write(soap.getBytes());

            InputStream is = conn.getInputStream();

            byte[] b = new byte[1024];
            int len = 0;
            
            while ((len = is.read(b)) != -1) {
                String ss = new String(b, 0, len, "UTF-8");
                result+=ss;
            }

            is.close();
            os.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return result;
    }
    
    private void setjo_po(JSONObject jo,MboRemote po) throws MXException,RemoteException, JSONException{
		jo.put("ponum", po.getString("ponum"));
		jo.put("description", po.getString("description"));
		jo.put("purchaseagent", po.getString("purchaseagent"));
		jo.put("orderdate", ldateToString(po.getDate("orderdate")));
		jo.put("requireddate", ldateToString(po.getDate("requireddate")));
		jo.put("followupdate", ldateToString(po.getDate("followupdate")));
		jo.put("potype", po.getString("potype"));
		jo.put("originalponum", po.getString("originalponum"));
		jo.put("status", po.getString("status"));
		jo.put("statusdate", ldateToString(po.getDate("statusdate")));
		jo.put("vendor", po.getString("vendor"));
		jo.put("contact", po.getString("contact"));
		jo.put("freightterms", po.getString("freightterms"));
		jo.put("paymentterms", po.getString("paymentterms"));
		jo.put("shipvia", po.getString("shipvia"));
		jo.put("customernum", po.getString("customernum"));
		jo.put("fob", po.getString("fob"));
		jo.put("shipto", po.getString("shipto"));
		jo.put("shiptoattn", po.getString("shiptoattn"));
		jo.put("billto", po.getString("billto"));
		jo.put("billtoattn", po.getString("billtoattn"));
		jo.put("totalcost", String.valueOf(po.getDouble("totalcost")));
		jo.put("changeby", po.getString("changeby"));
		jo.put("changedate", ldateToString(po.getDate("changedate")));
		jo.put("priority", String.valueOf(po.getInt("priority")));
		jo.put("historyflag", po.getString("historyflag"));
		jo.put("po1", po.getString("po1"));
		jo.put("po2", po.getString("po2"));
		jo.put("po3", po.getString("po3"));
		jo.put("po4", po.getString("po4"));
		jo.put("po5", po.getString("po5"));
		jo.put("po6", String.valueOf(po.getDouble("po6")));
		jo.put("po7", ldateToString(po.getDate("po7")));
		jo.put("po8", ldateToString(po.getDate("po8")));
		jo.put("po9", String.valueOf(po.getInt("po9")));
		jo.put("po10", po.getString("po10"));
		jo.put("vendeliverydate", ldateToString(po.getDate("vendeliverydate")));
		jo.put("receipts", po.getString("receipts"));
		jo.put("currencycode", po.getString("currencycode"));
		jo.put("exchangerate", String.valueOf(po.getDouble("exchangerate")));
		jo.put("exchangedate", ldateToString(po.getDate("exchangedate")));
		jo.put("buyahead", po.getString("buyahead"));
		jo.put("totaltax1", String.valueOf(po.getDouble("totaltax1")));
		jo.put("totaltax2", String.valueOf(po.getDouble("totaltax2")));
		jo.put("totaltax3", String.valueOf(po.getDouble("totaltax3")));
		jo.put("inclusive1", po.getString("inclusive1"));
		jo.put("inclusive2", po.getString("inclusive2"));
		jo.put("inclusive3", po.getString("inclusive3"));
		jo.put("internal", po.getString("internal"));
		jo.put("totaltax4", String.valueOf(po.getDouble("totaltax4")));
		jo.put("totaltax5", String.valueOf(po.getDouble("totaltax5")));
		jo.put("inclusive4", po.getString("inclusive4"));
		jo.put("inclusive5", po.getString("inclusive5"));
		jo.put("startdate", ldateToString(po.getDate("startdate")));
		jo.put("enddate", ldateToString(po.getDate("enddate")));
		jo.put("payonreceipt", po.getString("payonreceipt"));
		jo.put("buyercompany", po.getString("buyercompany"));
		jo.put("exchangerate2", String.valueOf(po.getDouble("exchangerate2")));
		jo.put("mnetsent", po.getString("mnetsent"));
		jo.put("ecomstatusdate", ldateToString(po.getDate("ecomstatusdate")));
		jo.put("sourcesysid", po.getString("sourcesysid"));
		jo.put("ownersysid", po.getString("ownersysid"));
		jo.put("externalrefid", po.getString("externalrefid"));
		jo.put("sendersysid", po.getString("sendersysid"));
		jo.put("siteid", po.getString("siteid"));
		jo.put("orgid", po.getString("orgid"));
		jo.put("description_longdescription", po.getString("description_longdescription"));
		jo.put("freightterms_longdescription", po.getString("freightterms_longdescription"));
		jo.put("receivedtotalcost", String.valueOf(po.getDouble("receivedtotalcost")));
		jo.put("potypemode", po.getString("potypemode"));
		jo.put("totalbasecost", String.valueOf(po.getDouble("totalbasecost")));
		jo.put("pretaxtotal", String.valueOf(po.getDouble("pretaxtotal")));
		jo.put("contractrefnum", po.getString("contractrefnum"));
		jo.put("poid", String.valueOf(po.getInt("poid")));
		jo.put("contractrefid", String.valueOf(po.getInt("contractrefid")));
		jo.put("contractrefrev", String.valueOf(po.getInt("contractrefrev")));
		jo.put("contreleaseseq", String.valueOf(po.getInt("contreleaseseq")));
		jo.put("storeloc", po.getString("storeloc"));
		jo.put("storelocsiteid", po.getString("storelocsiteid"));
		jo.put("inspectionrequired", po.getString("inspectionrequired"));
		jo.put("np_statusmemo", po.getString("np_statusmemo"));
		jo.put("langcode", po.getString("langcode"));
		jo.put("hasld", po.getString("hasld"));
		jo.put("statusiface", po.getString("statusiface"));
		jo.put("revisionnum", String.valueOf(po.getInt("revisionnum")));
		jo.put("revcomments", po.getString("revcomments"));
		jo.put("internalchange", po.getString("internalchange"));
		jo.put("allowreceipt", po.getString("allowreceipt"));
		jo.put("ignorecntrev", po.getString("ignorecntrev"));
		jo.put("revcomments_longdescription", po.getString("revcomments_longdescription"));
		jo.put("udapptype", po.getString("udapptype"));
		jo.put("udcompany", po.getString("udcompany"));
		jo.put("uddept", po.getString("uddept"));
		jo.put("udofs", po.getString("udofs"));
		jo.put("udcreateby", po.getString("udcreateby"));
		jo.put("udcreatetime", ldateToString(po.getDate("udcreatetime")));
		jo.put("udiscon", po.getString("udiscon"));
		jo.put("udconnum", po.getString("udconnum"));
		jo.put("udpurplat", po.getString("udpurplat"));
		jo.put("uddirector", po.getString("uddirector"));
		jo.put("udserialnum", po.getString("udserialnum"));
		jo.put("udpokeynum", po.getString("udpokeynum"));
		jo.put("udmatstatus", po.getString("udmatstatus"));
		jo.put("udremark", po.getString("udremark"));
		jo.put("udcategory", po.getString("udcategory"));
		jo.put("udcurrency", po.getString("udcurrency"));
		jo.put("uddeliverydate", ldateToString(po.getDate("uddeliverydate")));
		jo.put("udpredicttaxcost", String.valueOf(po.getDouble("udpredicttaxcost")));
		jo.put("udauthorizer", po.getString("udauthorizer"));
		jo.put("udbudat", ldateToString(po.getDate("udbudat")));
		jo.put("udukurs", String.valueOf(po.getDouble("udukurs")));
		jo.put("oldnum", po.getString("oldnum"));
		jo.put("days", String.valueOf(po.getInt("days")));
		jo.put("dateofdelivery", ldateToString(po.getDate("dateofdelivery")));
		jo.put("accepmethod", po.getString("accepmethod"));
		jo.put("promethod", po.getString("promethod"));
		jo.put("paymethod", po.getString("paymethod"));
		jo.put("bigproject", po.getString("bigproject"));
		jo.put("udcgly", po.getString("udcgly"));
		jo.put("udcglb", po.getString("udcglb"));
		jo.put("udrevponum", po.getString("udrevponum"));
		jo.put("udrevnum", String.valueOf(po.getInt("udrevnum")));
		jo.put("udceo", po.getString("udceo"));
		jo.put("udcfo", po.getString("udcfo"));
		jo.put("udevaluate", po.getString("udevaluate"));
		jo.put("udpaidcost", String.valueOf(po.getDouble("udpaidcost")));
		jo.put("udpronum", po.getString("udpronum"));
		jo.put("udproperties", po.getString("udproperties"));
		jo.put("udsfdg", po.getString("udsfdg"));
		jo.put("udprojectnum", po.getString("udprojectnum"));
		jo.put("udcapex", po.getString("udcapex"));
    }
    
	public static String ldateToString(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
			String time = sdf.format(date);
			return time;
		} else {
			return "";
		}
	}
	
	public void addImportLog(String logType, String logContent) {
		this.isImportLog = (this.isImportLog + "\r\n" + this.isDate  + logType + "\t" + logContent);
	}
	
	public void saveImportLog(String table) throws MXApplicationException {
		try {
			Calendar calendar = Calendar.getInstance();
			Date rt = calendar.getTime();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmm");
			String lsDate = dateFormat.format(rt);
			String s3 = MXServer.getMXServer().getProperty("mxe.doclink.doctypes.defpath");
			//cron为路径
			String filePath = s3 + "\\DATAHOUSE\\" + lsDate + table + ".log";

			log("filePath", filePath);
			try {
				File myFilePath = new File(filePath);
				if (!myFilePath.exists()) {
					myFilePath.createNewFile();
				}
				FileWriter resultFile = new FileWriter(myFilePath); //此方法如果log文件名同名，那么会覆盖
				//FileWriter resultFile = new FileWriter(myFilePath,true);//此方法会在同名的log文件上继续累加
				PrintWriter myFile = new PrintWriter(resultFile);
				myFile.println(this.isImportLog);
				myFile.close();
				resultFile.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new MXApplicationException("", "日志文件写入失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MXApplicationException("", "日志文件写入失败！");
		}
	}
	
	public void log(String name, String value) {
		System.out.print("\n" + getClass().getName() + " " + this.isDate + " : " + name + "=" + value);
	}
}
