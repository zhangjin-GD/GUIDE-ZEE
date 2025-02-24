package guide.app.workorder;

import guide.app.common.CommonUtil;
import guide.app.pr.UDPRLine;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import psdi.app.workorder.WO;
import psdi.app.workorder.WORemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.workflow.WorkFlowServiceRemote;

public class UDWO extends WO implements WORemote {

	private static final int KEYLEN = 2;

	public UDWO(MboSet ms) throws RemoteException, MXException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			String status = getString("status");
			if (getMboValue("JPASSETS").getDefault() == null) {
				setValue("JPASSETS", false, 2L);
			}
			setValue("udvstarttime", MXServer.getMXServer().getDate(), 11L);
			
			// String reportedby = getString("reportedby");
			// if (status != null && status.equalsIgnoreCase("WAPPR")
			// && reportedby != null &&
			// !reportedby.equalsIgnoreCase(getUserInfo().getPersonId())) {
			// setFlags(7L);
			// }
			String[] attrs1 = { "worktype" };
			String[] attrs2 = { "actstart", "actfinish" };

			if (this.toBeAdded()) {
				this.setFieldFlag(attrs1, 7L, false);
				this.setFieldFlag(attrs2, 7L, true);
			} else {
				this.setFieldFlag(attrs1, 7L, true);
				if ("WAPPR".equalsIgnoreCase(status)) {
					this.setFieldFlag(attrs2, 7L, true);
				} else {
					this.setFieldFlag(attrs2, 7L, false);
				}
			}
			
			/**
			 * @function:ZEE-泽港工单权限控制
			 * @date:2023-07-24 14:40:43
			 */
//			String appname = getThisMboSet().getApp();
//			if (appname == null) {
//				return;
//			}
//			String[] str = { "description", "udassettypecode","assetnum","jpnum","udtlnum","uddescription","targstartdate","targcompdate","actstart","actfinish","udworktype2","udhandler","udprojectnum" }; //主信息
//			String[] str1 = {"udfailmech","udfailcausedesc","udfailproblemdesc","udfailremedydesc"}; //故障
//			String [] str2 = {"udremark"};
//			if ("UDWOZEE".equalsIgnoreCase(appname)) {
//				String udworktype2 = getString("udworktype2");
//				String personid = getUserInfo().getPersonId();
//				String reporter = getString("reportedby");
//
//				setFieldFlag(str, 7L, true);
//				setFieldFlag(str1, 7L, true);
//				setFieldFlag(str2, 7L, true);
//
//				if (udworktype2.equalsIgnoreCase("PM")) {
//					
//					if (status.equalsIgnoreCase("PLANNED") && inwflow()) {
//						setFieldFlag(str, 7L, false);
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					if (status.equalsIgnoreCase("RELEASED") && inwflow()) {
//						setFieldFlag(str, 7L, false);
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					if (status.equalsIgnoreCase("INPROGRESS") && inwflow()) {
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					if (status.equalsIgnoreCase("WFPARTS") && inwflow()) {
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					if (status.equalsIgnoreCase("WOBACK") && inwflow()) {
//						setFieldFlag(str, 7L, false);
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//				} else if (udworktype2.equalsIgnoreCase("CM")) { //CM
//					
//					if ((!reporter.equalsIgnoreCase(personid) && !personid.equalsIgnoreCase("MAXADMIN")) && !isNew() && status.equalsIgnoreCase("PLANNED")) {
//						setFlag(7L, true);
//					} else if ((reporter.equalsIgnoreCase(personid) || personid.equalsIgnoreCase("MAXADMIN")) && !isNew() && status.equalsIgnoreCase("PLANNED")) {
//						setFieldFlag(str, 7L, false);
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					
//					if (status.equalsIgnoreCase("")) {
//						setFieldFlag(str, 7L, false);
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					if (status.equalsIgnoreCase("RELEASED") && inwflow()) {
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					if (status.equalsIgnoreCase("INPROGRESS") && inwflow()) {
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					if (status.equalsIgnoreCase("WFPARTS") && inwflow()) {
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					if (status.equalsIgnoreCase("WOBACK") && inwflow()) {
//						setFieldFlag(str, 7L, false);
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//				} else { //非PM、CM
//					if ((!reporter.equalsIgnoreCase(personid) && !personid.equalsIgnoreCase("MAXADMIN"))&& !isNew()&& status.equalsIgnoreCase("INPROG")) {
//						setFlag(7L, true);
//					} else if ((reporter.equalsIgnoreCase(personid) || personid.equalsIgnoreCase("MAXADMIN"))&& !isNew()&& status.equalsIgnoreCase("INPROG")) {
//						setFieldFlag(str, 7L, false);
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//						setFieldFlag("udhandler", 128L, true);
//					}
//					
//					if (status.equalsIgnoreCase("")) {
//						setFieldFlag(str, 7L, false);
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					if (status.equalsIgnoreCase("WFPARTS") && inwflow()) {
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//					if (status.equalsIgnoreCase("WOBACK") && inwflow()) {
//						setFieldFlag(str, 7L, false);
//						setFieldFlag(str1, 7L, false);
//						setFieldFlag(str2, 7L, false);
//					}
//				}
//			}
			
			/** 
			 * ZEE - 工单capex&project-code
			 * 2025-2-18  11:17  
			 * 184-203
			 */
		    String appname = getThisMboSet().getApp();
			if (appname == null) {
				return;
			}
		    if (appname!=null && appname.equalsIgnoreCase("UDWOZEE")) {
				if(getString("udcompany").equalsIgnoreCase("ZEE")){
					String udcapex = getString("udcapex");
					if (udcapex.equalsIgnoreCase("N")) {
						setValue("udprojectnum", "", 11L);
						setFieldFlag("udprojectnum", 128L, false); // 取消必填
					}else if(udcapex.equalsIgnoreCase("Y") ){
						setFieldFlag("udprojectnum", 128L, true); // 设置必填
					}
				}
		     }
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		this.setValue("udnotshutdown", false, 11L);
		this.setValue("udwostatus", "FAULT", 11L);
		String appName = this.getThisMboSet().getApp();
		if (appName != null && !appName.isEmpty()) {
			String udcompany = getString("UDCOMPANY");
			if (appName.equalsIgnoreCase("UDWOEM") || appName.equalsIgnoreCase("UDWOEMRPT")) {
				this.setValue("worktype", "EM", 2L);
				this.setValue("onbehalfofid", this.getUserInfo().getPersonId(), 2L);
				this.setValue("targstartdate", MXServer.getMXServer().getDate(), 11L);
			} else if (appName.equalsIgnoreCase("UDWOCM")) {
				this.setValue("worktype", "CM", 2L);
				// 码头为泉州，计划开始时间为第二天的早上8:30,计划结束时间为下午5点
				if ("2528QPCT".equalsIgnoreCase(udcompany)) {
					Date sysdate = MXServer.getMXServer().getDate();
					// 设置第二天早上8点半
					Calendar calendar1 = Calendar.getInstance();
					calendar1.setTime(sysdate);
					calendar1.add(Calendar.DATE, 1);
					calendar1.set(Calendar.HOUR_OF_DAY, 8);
					calendar1.set(Calendar.MINUTE, 30);
					calendar1.set(Calendar.SECOND, 0);
					Date targstartdate = calendar1.getTime();
					// 设置第二天早上8点半
					Calendar calendar2 = Calendar.getInstance();
					calendar2.setTime(sysdate);
					calendar2.add(Calendar.DATE, 1);
					calendar2.set(Calendar.HOUR_OF_DAY, 17);
					calendar2.set(Calendar.MINUTE, 30);
					calendar2.set(Calendar.SECOND, 0);
					Date targcompdate = calendar2.getTime();

					this.setValue("targstartdate", targstartdate, 2L);
					this.setValue("targcompdate", targcompdate, 2L);
				}
			} else if (appName.equalsIgnoreCase("UDWOFM")) {
				this.setValue("worktype", "FM", 2L);
				this.setValue("udassettypecode", "FAC", 2L);
			} else if (appName.equalsIgnoreCase("UDWOSW")) {
				this.setValue("worktype", "SW", 2L);
			}
			if (appName.equalsIgnoreCase("UDWOCM") || appName.equalsIgnoreCase("UDWOFM")) {
				UDGwoTaskSet gwoTaskSet = (UDGwoTaskSet) this.getMboSet("UDGWOTASK");
				UDGwoTask gwoTask = (UDGwoTask) gwoTaskSet.add();
				getMboValue("wonum").autoKey();
				gwoTask.setValue("wonum", getString("wonum"), 11L);
			}
			if ("AE03ADT".equalsIgnoreCase(udcompany) || "GR02PCT".equalsIgnoreCase(udcompany)) {
				this.setValue("status", "INPRG", 11L);
			}
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if (this.toBeAdded()) {
			String appName = this.getThisMboSet().getApp();
			String persongroup = "";
			String wonum = getString("wonum");
			String reportedBy = getString("reportedby");
			String lead = getString("lead");
			String ownerTable = getName();
			String ownerId = "" + getUniqueIDValue() + "".replaceAll(",", "");
			String autoWfHistoryId = CommonUtil.getValue("UDAUTOWFHISTORY",
					"ownertable='" + ownerTable + "' and ownerid=" + ownerId + "", "ownerid");
			String status = this.getString("status");
			String worktype = this.getString("worktype");
			String udcompany = this.getString("udcompany");
			MboSetRemote personSet = this.getMboSet("$PERSONGROUP", "PERSONGROUP", "uddept = '" + udcompany + "' ");
			if ((personSet != null) && (!(personSet.isEmpty()))) {
				persongroup = personSet.getMbo(0).getString("persongroup");
			}
			String prefix = persongroup + "-" + worktype;
			if (worktype != null && persongroup != null && !worktype.isEmpty() && !persongroup.isEmpty()) {
				String keyNum = CommonUtil.autoKeyNum("WORKORDER", "UDWONUM", prefix, "yyMMdd", KEYLEN);
				this.setValue("udwonum", keyNum, 11L);// 设置工单编号
			}
			System.out.println("\n---------------------" + autoWfHistoryId);
			if (autoWfHistoryId == null && status != null && status.equalsIgnoreCase("WAPPR")) {
				// 故障报修自动发送流程
				if ("UDWOEMRPT".equalsIgnoreCase(appName)) {
					WorkFlowServiceRemote serviceRemote = (WorkFlowServiceRemote) MXServer.getMXServer()
							.lookup("WORKFLOW");
					boolean isEnabled = serviceRemote.isActiveProcess("UDWOEM", "WORKORDER", this.getUserInfo());
					if (isEnabled && serviceRemote.getActiveInstances(this).isEmpty()) {
						try {
							serviceRemote.initiateWorkflow("UDWOEM", this);
						} catch (Exception e) {
						}
					}
				} else if (appName == null || appName.equalsIgnoreCase("")) {
					// 接口同步自动发送流程
					if (reportedBy.equalsIgnoreCase("TM") || reportedBy.equalsIgnoreCase("QPCTEMRPT")
							|| reportedBy.equalsIgnoreCase("JPPDCEMRPT") || reportedBy.equalsIgnoreCase("NTTHEMRPT")) {
						CommonUtil.insertAutoWf(ownerTable, ownerId, reportedBy);
						if (reportedBy.equalsIgnoreCase("QPCTEMRPT") && lead != null && !lead.equalsIgnoreCase("")) { // 默认带出维修员工
							MboSetRemote wplaborSet = MXServer.getMXServer().getMboSet("UDWPLABOR",
									MXServer.getMXServer().getSystemUserInfo());
							MboRemote wplabor = wplaborSet.add();
							wplabor.setValue("wonum", wonum, 11L);
							wplabor.setValue("laborcode", lead, 11L);
							wplaborSet.save();
							wplaborSet.close();
						}
						WorkFlowServiceRemote serviceRemote = (WorkFlowServiceRemote) MXServer.getMXServer()
								.lookup("WORKFLOW");
						boolean isEnabled = serviceRemote.isActiveProcess("UDWOEM", "WORKORDER", this.getUserInfo());
						if (isEnabled && serviceRemote.getActiveInstances(this).isEmpty()) {
							try {
								serviceRemote.initiateWorkflow("UDWOEM", this);
							} catch (Exception e) {
								CommonUtil.ifaceLog("自动发送流程失败", reportedBy, ownerTable, ownerId, wonum, e.toString());
							}
						}
					}
				}
			}
		}
		if (isModified("actfinish")
				&& (getInitialValue("actfinish").asString() == null
						|| getInitialValue("actfinish").asString().equalsIgnoreCase(""))
				&& getString("actfinish") != null && !getString("actfinish").equalsIgnoreCase("")
				&& !getBoolean("udconfirm")) {
			if (getString("udcompany").equalsIgnoreCase("3120GOCT")) {
				String flag = emGoctConfirm();
				try {
					flag = CommonUtil.getString(new JSONObject(flag), "code");
				} catch (JSONException e) {
					flag = e.toString();
					e.printStackTrace();
				}
				if (flag.equalsIgnoreCase("success") || flag.equalsIgnoreCase("200")) {
					setValue("udconfirm", 1, 11L);
				}
			}
		}
		String woanalysis = getString("udwoanalysis");
		if ("4MISREPORT".equalsIgnoreCase(woanalysis)
				&& !getMboValue("status").getInitialValue().asString().equalsIgnoreCase("COMP")
				&& getString("status").equalsIgnoreCase("COMP")) {
			setValueNull("actstart");
			setValueNull("actfinish");
		}
		
		/**
		 * ZEE-保存时生成二维码
		 *  YS   2023-07-25 16:43:41
		 */
		//二维码储存信息
		String property = MXServer.getMXServer().getProperty("mxe.doclink.doctypes.defpath");
		int workorderid = getInt("workorderid");
//		String url = "wonum : "+getString("wonum");//二维码内容
//      String url = "{" +"\"woNum\":\""+getString("wonum")+"\"" +"}";//二维码内容
		String url = getString("wonum");//二维码内容
//		String path = FileSystemView.getFileSystemView().getHomeDirectory() + File.separator + "testQrcode";
		String path =""+property+"\\QRCode\\WORKORDER";
	   // path="D:\\DOCLINKS\\A";//保存到本地的路径
		String fileName = getString("wonum")+".jpg";//文件名
	    createQrCode(url, path, fileName);
	    String url1=path+"\\"+fileName;
	    setValue("udurlpath", url1, 11L);
		File f = new File(url1);
	    BufferedImage bi; 
	     try {   
	            bi = ImageIO.read(f);   
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();   
	            ImageIO.write(bi, "jpg", baos);   
	            byte[] bytes = baos.toByteArray();
	            setValue("udimge", bytes, 11L);
	            MboSetRemote imglibSet = this.getMboSet("IMGLIB");
	            imglibSet.setWhere("refobject='WORKORDER' and refobjectid='"+workorderid+"'");
	            imglibSet.reset();
	            if(imglibSet.isEmpty()){
	            	MboRemote imglib = imglibSet.add();
	            	imglib.setValue("imagename", fileName, 11L);
	            	imglib.setValue("mimetype", "image/jpeg", 11L);
	            	imglib.setValue("image", bytes, 11L);
	            }else{
	            	MboRemote imglib = imglibSet.getMbo(0);
	            	imglib.setValue("imagename", fileName, 11L);
	            }
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        }
	     
	     /**
	      * ZEE-工单-预防性维护生成的工单,将工单完成时的实际仪表读书累计到相应的PM
	      * 2024-02-22 10:08:24
	      **/
	     String appname = getThisMboSet().getApp();
	     if (appname!=null && appname.equalsIgnoreCase("UDWOZEE")) {
			String status = getString("status");
			if (status.equalsIgnoreCase("COMP")) {
				//将此时的仪表度数存到工单
				String assetnum = getString("assetnum");
				MboSetRemote udzeerunrecordSet = MXServer.getMXServer().getMboSet("UDZEERUNRECORD",MXServer.getMXServer().getSystemUserInfo());
				udzeerunrecordSet.setWhere(" assetnum='"+assetnum+"' order by meterrecord desc ");
				udzeerunrecordSet.reset();
				if (!udzeerunrecordSet.isEmpty() && udzeerunrecordSet.count() > 0) {
					MboRemote udzeerunrecord = udzeerunrecordSet.getMbo(0);
					double record = udzeerunrecord.getDouble("meterrecord");
					setValue("udzeelastmeter", record, 11L);
				}
				udzeerunrecordSet.close();
				
				
				//将工单COMP是的仪表度数更新到PM
				double udzeelastmeter = getDouble("udzeelastmeter");
				MboRemote pm = getMboSet("PM").getMbo(0);
				if (pm == null) {
					return;
				}
				MboRemote pmMeter = pm.getMboSet("PMMETER").getMbo(0); //与当前工单相关联的PmMeter记录
				if (pmMeter == null) {
					return;
				}
				double frequency = pmMeter.getDouble("frequency"); //仪表频率
				double newRead = udzeelastmeter + frequency;
				pmMeter.setValue("readingatnextwo", newRead, 2L); //估算的下一到期日
				pmMeter.setValue("ltdreadatnextwo", newRead, 2L);
				
				pmMeter.setValue("lastpmwogenread", udzeelastmeter, 2L);
				pmMeter.setValue("ltdlastpmworead", udzeelastmeter, 2L);
			}
		}
	}
	
	
	 public static String createQrCode(String url, String path, String fileName) {
	        try {
	            Map<EncodeHintType, String> hints = new HashMap<>();
	            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	            BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 250, 250, hints);
	            File file = new File(path, fileName);
	            if (file.exists() || ((file.getParentFile().exists() || file.getParentFile().mkdirs()) && file.createNewFile())) {
	                writeToFile(bitMatrix, "jpg", file);
	                System.out.println("-------success-----：" + file);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	 
	    static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
	        BufferedImage image = toBufferedImage(matrix);
	        if (!ImageIO.write(image, format, file)) {
	            throw new IOException("Could not write an image of format " + format + " to " + file);
	        }
	    }
	    
		private static final int BLACK = 0xFF000000;
	    private static final int WHITE = 0xFFFFFFFF;

	    private static BufferedImage toBufferedImage(BitMatrix matrix) {
	        int width = matrix.getWidth();
	        int height = matrix.getHeight();
	        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height; y++) {
	                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
	            }
	        }
	        return image;
	    }

	public String emGoctConfirm() throws RemoteException {
		String epUrl = MXServer.getMXServer().getProperty("guide.ep.goct.url");
		if (epUrl == null || epUrl.equalsIgnoreCase(""))
			epUrl = "http://kos.goct.com.cn:8086/apiServer/eamEquipment/closeWorkOrder";
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "{\"code\":\"提示， 接口回传失败！\"}";
		try {
			URL realUrl = new URL(epUrl);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print("eamId=" + getString("wonum"));
			out.flush();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result = new String(line.getBytes(), "UTF-8");
			}
		} catch (Exception e) {
			result = "{\"code\":\"提示， 接口回传失败！" + e.toString() + "\"}";
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public String createPrMat() throws RemoteException, MXException {
		String prnum = "";
		String wonum = this.getString("wonum");
		String wodesc = this.getString("description");
		String personid = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		MboSetRemote prSet = this.getMboSet("$PR", "PR", "udrefwo='" + wonum + "' and status not in ('CAN')");
		if (!prSet.isEmpty() && prSet.count() > 0) {
			MboRemote pr = prSet.getMbo(0);
			prnum = pr.getString("prnum");
		} else {
			MboRemote pr = prSet.add();
			pr.setValue("udapptype", "PRMAT", 11L);
			pr.setValue("udmatstatus", "SPORADIC", 11L);
			pr.setValue("description", wodesc, 11L);
			pr.setValue("exchangerate", 1, 11L);
			pr.setValue("exchangedate", currentDate, 11L);
			pr.setValue("udcreateby", personid, 2L);// 创建人
			pr.setValue("udcreatetime", currentDate, 11L);// 创建时间
			pr.setValue("requireddate", CommonUtil.getCalDate(currentDate, 14), 2L);// 要求日期，默认14天后
			pr.setValue("udrefwo", wonum, 11L);// 创建时间
			String udcompany = pr.getString("udcompany");
			MboSetRemote prlineSet = pr.getMboSet("PRLINE");
			MboSetRemote gjobMatSet = this.getMboSet("UDGJOBMATERIAL");
			if (!gjobMatSet.isEmpty() && gjobMatSet.count() > 0) {
				for (int i = 0; gjobMatSet.getMbo(i) != null; i++) {
					MboRemote gjobMat = gjobMatSet.getMbo(i);
					String itemnum = gjobMat.getString("itemnum");
					double orderqty = gjobMat.getDouble("orderqty");

					UDPRLine prline = (UDPRLine) prlineSet.add();
					prline.setValue("itemnum", itemnum, 2L);
					String tax1code = CommonUtil.getValue("UDDEPT", "type='COMPANY' and deptnum='" + udcompany + "'",
							"TAX1CODE");
					prline.setValue("tax1code", tax1code, 2L);
					prline.setValue("orderqty", orderqty, 2L);
					prline.setValue("refwo", wonum, 11L);
				}
			}
			prnum = pr.getString("prnum");
		}
		return prnum;
	}
	
	public boolean inwflow() throws RemoteException, MXException {
		boolean flag = false;
		MboSetRemote wf = this.getMboServer().getMboSet("wfassignment",
				getUserInfo());
		wf.setWhere("ASSIGNSTATUS='ACTIVE' and OWNERTABLE = '" + this.getName()
				+ "' and OWNERID = '" + this.getUniqueIDValue()
				+ "' and ASSIGNCODE = '" + this.getUserInfo().getPersonId()
				+ "'");
		if (wf != null && wf.count() > 0) {
			flag = true;
		}
		wf.close();
		return flag;
	}

}
