package guide.webclient.beans.receipts;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.mail.MessagingException;

import org.json.JSONException;
import org.json.JSONObject;

import guide.app.common.ComExecute;
import guide.app.common.CommonUtil;
import guide.app.fixed.FixEd;
import guide.app.fixed.FixEdSet;
import guide.app.inventory.UDMatRecTrans;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.receipts.ReceiptsTableBean;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.WebClientEvent;

import java.util.regex.Pattern;

import org.json.JSONArray;

import java.util.Date;
import java.util.regex.Matcher;

public class UDReceiptsTableBean extends ReceiptsTableBean {

	public void udrecnum() throws MXException, RemoteException {
		MboRemote mboremote = this.parent.getMbo();
		if (mboremote.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
	}

	@Override
	public int selorditem() throws MXException, RemoteException {
//		MboRemote mboremote = this.parent.getMbo();
//		if (mboremote.toBeSaved()) {
//			throw new MXApplicationException("guide", "1041");
//		}
//
//		MboSetRemote polineSet = mboremote.getMboSet("POLINE");
//		if (!polineSet.isEmpty() && polineSet.count() > 0) {
//			Set<String> hashSet = new LinkedHashSet<String>();
//			for (int i = 0; polineSet.getMbo(i) != null; i++) {
//				MboRemote poline = polineSet.getMbo(i);
//				if (!poline.isNull("storeloc")) {
//					String storeloc = poline.getString("storeloc");
//					hashSet.add(storeloc);
//				}
//			}
//			if (hashSet.size() > 1) {
//				WebClientEvent event = this.clientSession.getCurrentEvent();
//				this.clientSession.showMessageBox(event, "guide", "1130", (Object[]) null);
//			}
//		}

		return super.selorditem();
	}

	public int origSendVendor() throws MXException, RemoteException, JSONException {
		MboRemote mbo = this.parent.getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		String flag = "无收发人或收发件信息，未发送邮件！";
		DataBean db = app.getDataBean("main_matreceiptstable");
		MboRemote linembo = db.getMbo(db.getCurrentRow());

		if (linembo != null) {
			String recNum = linembo.getString("udrecnum");
			String toAddress = CommonUtil.getValue(mbo, "VENDOR", "udemail");
			String fromAddress = CommonUtil.getEmail(mbo.getUserInfo().getPersonId());
			String title = "订单" + mbo.getString("ponum") + "，已" + mbo.getString("RECEIPTS.description") + "，请提供相应发票。";
			String message = title;

			MboSetRemote recNumSet = linembo.getMboSet("UDRECNUM");
			if (!recNumSet.isEmpty() && recNumSet.count() > 0) {
				message = "订单编号：" + mbo.getString("ponum") + "，采购员：" + mbo.getString("PURCHASE.displayname") + "；"
						+ "\n订单不含税金额：" + (mbo.getDouble("totalcost") - mbo.getDouble("totaltax1")) + "，订单税额："
						+ mbo.getDouble("totaltax1") + "；" + "\n本次接收不含税金额：" + recNumSet.sum("linecost") + "，本次接收税额："
						+ recNumSet.sum("tax1") + "。";

				System.out.println("\n---------fromAddress：" + fromAddress + "---------toAddress：" + toAddress);
				if (fromAddress != null && !fromAddress.equalsIgnoreCase("") && toAddress != null
						&& !toAddress.equalsIgnoreCase("")) {
					JSONObject paramRpt = new JSONObject();
					paramRpt.put("reportName", "ud_rkddjalltax.rptdesign");
					paramRpt.put("description", "入库单");
					paramRpt.put("appName", "UDRECPOM");
					paramRpt.put("keyNum", recNum);
					JSONObject paramData = new JSONObject();
					paramData.put("recnum", recNum);
					File attachment = CommonUtil.getReport(clientSession.getUserInfo(), paramRpt, paramData);
					try {
						// MXServer.sendEMail(toAddress, fromAddress, title, message);
						MXServer.sendEMail(toAddress, null, null, fromAddress, title, message, null,
								new String[] { attachment.getAbsolutePath() }, (String[]) null);
						flag = "入库单" + recNum + "邮件已发送成功！";
					} catch (MessagingException e) {
						flag = "邮件发送失败：" + e;
						e.printStackTrace();
					}
				}

			}

		}

		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", flag, 1);
		return 1;
	}

	public int sendVendor() throws MXException, RemoteException, JSONException {
		MboRemote mbo = this.parent.getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		String flag = "提示，没有可发送的入库记录！";
		DataBean db = app.getDataBean("main_matreceiptstable");
		MboRemote linembo = db.getMbo(db.getCurrentRow());

		if (linembo != null) {
			MboSetRemote recNumSet = linembo.getMboSet("UDRECNUM");
			if (!recNumSet.isEmpty() && recNumSet.count() > 0) {
				String recNum = linembo.getString("udrecnum");
				String toAddress = CommonUtil.getValue(mbo, "VENDOR", "udemail");
				String title = "订单" + mbo.getString("ponum") + "，已" + mbo.getString("RECEIPTS.description")
						+ "，请提供相应发票。";
				String message = "订单编号：" + mbo.getString("ponum") + "，采购员：" + mbo.getString("PURCHASE.displayname")
						+ "；" + "\n订单不含税金额：" + (mbo.getDouble("totalcost") - mbo.getDouble("totaltax1")) + "，订单税额："
						+ mbo.getDouble("totaltax1") + "；" + "\n本次接收不含税金额：" + recNumSet.sum("linecost") + "，本次接收税额："
						+ recNumSet.sum("tax1") + "。";

				System.out.println("\n---------toAddress：" + toAddress);
				if (toAddress != null && !toAddress.equalsIgnoreCase("")) {
					// 报表参数
					JSONObject paramRpt = new JSONObject();
					paramRpt.put("reportName", "ud_rkddjalltax.rptdesign");
					paramRpt.put("description", "入库单通知单");
					paramRpt.put("appName", "UDRECPOM");
					paramRpt.put("keyNum", recNum);
					JSONObject paramData = new JSONObject();
					paramData.put("recnum", recNum);
					// 报表执行
					File attachment = CommonUtil.getReport(clientSession.getUserInfo(), paramRpt, paramData);
					// 消息参数
					String personId = mbo.getUserInfo().getPersonId();
					String currentDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");
					JSONObject jsonData = new JSONObject();
					jsonData.put("id", recNum);
					jsonData.put("to_user", toAddress);
					jsonData.put("subject", title);
					jsonData.put("content", message);
					jsonData.put("create_time", currentDate);
					jsonData.put("create_by", personId);
					jsonData.put("change_time", currentDate);
					jsonData.put("change_by", personId);
					jsonData.put("file_path", attachment.getAbsolutePath());
					// 消息执行
					String returnResult = CommonUtil.sendGDEam(MXServer.getMXServer().getProperty("guide.gdnotify.url"),
							jsonData);// :6001/v1/api/notify
					String returnCode = CommonUtil.getString(new JSONObject(returnResult), "code");
					if (returnCode != null && returnCode.equalsIgnoreCase("200")) {
						flag = "入库单通知单" + recNum + "邮件已发送成功！";
					} else {
						flag = "入库单通知单" + recNum + "邮件发送失败："
								+ CommonUtil.getString(new JSONObject(returnResult), "result");
					}
				} else {
					flag = "提示，无收件人信息，未发送邮件！";
				}
			}
		}
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", flag, 1);
		return 1;
	}

	public int returnVendorMail() throws RemoteException, MXException, JSONException {
		MboRemote mbo = this.parent.getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		String flag = "";
		String personId = mbo.getUserInfo().getPersonId();
		String language = CommonUtil.getValue("PERSON", "status ='ACTIVE' and personid='" + personId + "'", "language");
		if (language.equalsIgnoreCase("EN")) {
			flag = "Prompt, there is no stock-in record to send";
		} else {
			flag = "提示，没有可发送的入库记录！";
		}
		String toAddress = CommonUtil.getValue(mbo, "VENDOR", "udemail");
		if (toAddress != null && !toAddress.equalsIgnoreCase("")) {
			DataBean db = app.getDataBean("main_matreceiptstable");
			MboRemote linembo = db.getMbo(db.getCurrentRow());
			if (linembo != null) {
				String issuetype = linembo.getString("issuetype");
				if ("RETURN".equalsIgnoreCase(issuetype)) {
					MboSetRemote recNumSet = linembo.getMboSet("UDRECNUM");
					if (!recNumSet.isEmpty() && recNumSet.count() > 0) {
						String recNum = linembo.getString("udrecnum");
						String ponum = mbo.getString("ponum");
						String purchasename = mbo.getString("PURCHASE.displayname");
						double sumLinecost = -recNumSet.sum("linecost");
						double sumTax1 = -recNumSet.sum("tax1");
						String currentDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");
						String title = "";
						if (language.equalsIgnoreCase("EN")) {
							title = "order" + ponum + ", please check the return information," + currentDate + "";
						} else {
							title = "订单" + ponum + "，请查收退货信息，" + currentDate + "";
						}
						StringBuilder contentStr = new StringBuilder();
						if (language.equalsIgnoreCase("EN")) {
							contentStr.append("Hello!\n");
							contentStr.append("  Order No.:" + ponum + ", Purchaser:" + purchasename + ";\n");
							contentStr.append("  Amount excluding tax received this time:" + sumLinecost
									+ ", tax received this time:" + sumTax1 + ";\n");
							contentStr.append(
									"  Serial number    Item No    Quantity    Amount excluding tax    Tax amount    Item description\n");
						} else {
							contentStr.append("您好！\n");
							contentStr.append("  订单编号：" + ponum + ",采购员：" + purchasename + "；\n");
							contentStr.append("  本次接收不含税金额：" + sumLinecost + "，本次接收税额：" + sumTax1 + "；\n");
							contentStr.append("  序号    物料编号    数量    不含税金额    税额    物料描述\n");
						}
						for (int i = 0; recNumSet.getMbo(i) != null; i++) {
							MboRemote recNumline = recNumSet.getMbo(i);
							int linenum = i + 1;
							String itemnum = recNumline.getString("itemnum");
							String udlongdesc = recNumline.getString("item.udlongdesc");
							double quantity = -recNumline.getDouble("quantity");
							double linecost = -recNumline.getDouble("linecost");
							double tax1 = -recNumline.getDouble("tax1");
							contentStr.append("  " + linenum + "      " + itemnum + "    " + quantity + "    "
									+ linecost + "    " + tax1 + "    " + udlongdesc + "\n");
						}
						if (language.equalsIgnoreCase("EN")) {
							contentStr.append(
									"Please confirm the information in time, thank you for your cooperation!\n");
						} else {
							contentStr.append("请及时确认信息，谢谢配合！\n");
						}
						// 消息参数
						JSONObject jsonData = new JSONObject();
						jsonData.put("id", recNum);
						jsonData.put("to_user", toAddress);
						jsonData.put("subject", title);
						jsonData.put("content", contentStr);
						jsonData.put("create_time", currentDate);
						jsonData.put("create_by", personId);
						jsonData.put("change_time", currentDate);
						jsonData.put("change_by", personId);
						jsonData.put("file_path", "");
						// 消息执行
						try {
							String returnResult = CommonUtil
									.sendGDEam(MXServer.getMXServer().getProperty("guide.gdnotify.url"), jsonData);
							String returnCode = CommonUtil.getString(new JSONObject(returnResult), "code");
							if (returnCode != null && returnCode.equalsIgnoreCase("200")) {
								if (language.equalsIgnoreCase("EN")) {
									flag = "Return notice:" + recNum + ", mail has been sent successfully!";
								} else {
									flag = "退货通知单：" + recNum + "，邮件已发送成功！";
								}

							} else {
								if (language.equalsIgnoreCase("EN")) {
									flag = "Return notice:" + recNum + ", mail sending failed:"
											+ CommonUtil.getString(new JSONObject(returnResult), "result");
								} else {
									flag = "退货通知单：" + recNum + "，邮件发送失败："
											+ CommonUtil.getString(new JSONObject(returnResult), "result");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							if (language.equalsIgnoreCase("EN")) {
								flag = "Return notice: " + recNum + ", mail sending failed.";
							} else {
								flag = "退货通知单：" + recNum + "，邮件发送失败。";
							}
						}
					}
				} else {
					if (language.equalsIgnoreCase("EN")) {
						flag = "Prompt, the selected line information is receipt information, not return information!";
					} else {
						flag = "提示，选中的行信息为入库信息，不是退货信息！";
					}

				}
			}
		} else {
			if (language.equalsIgnoreCase("EN")) {
				flag = "Prompt, no recipient information, no mail sent!";
			} else {
				flag = "提示，无收件人信息，未发送邮件！";
			}
		}
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", flag, 1);
		return 1;
	}

	// 添加 入库流水号
	public int rkdlsh() throws RemoteException, MXException {

		String sql = CommonUtil.getAttrs("1039");
		if (sql != null && !sql.equalsIgnoreCase("")) {
			try {
				ComExecute.executeSql(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 1;
	}
	
	/**
	 * ZEE - 逐条打印物资二维码
	 * DJY
	 * 31-117
	 * 2024-07-22 9:39:13
	 */	
public int UDZEEPRINT() throws MXException, RemoteException {
	MboRemote mbo = this.app.getAppBean().getMbo();
	String udcompany = mbo.getString("udcompany");
	if (udcompany != null && udcompany.equalsIgnoreCase("ZEE")) {
		DataBean db = app.getDataBean("main_matreceiptstable");
		MboRemote linembo = db.getMbo(db.getCurrentRow());
		if (linembo == null) {
			return 1;
		}
		String printUrl = MXServer.getMXServer().getProperty("guide.udzeePrint.url");
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();
		System.out.println("mbo----111----"+mbo);
		String udprintqty = getString("udprintqty");
		System.out.println("udprintqty----111----"+udprintqty);
		Integer ponum = mbo.getInt("ponum");
		String receivedunit = linembo.getString("receivedunit");
		String issueunit = linembo.getString("inventory.issueunit");
		String tobin = linembo.getString("udbinlocation");
		String itemnum = linembo.getString("itemnum");
		String description = linembo.getString("item.description");
		String udmatnum = linembo.getString("uditemcp.udmatnum");		
		Date transdate = linembo.getDate("transdate");
		String transdateStr = CommonUtil.getDateFormat(transdate, "yyyy-MM-dd");
		long invbalancesid = linembo.getLong("invbalances.invbalancesid");// ID
		//ZEE条形码key
		String udapikey = linembo.getString("uditemcp.udapikey");	
		Double quantity = Math.abs(linembo.getDouble("quantity"));
		if( getString("udprintqty").equalsIgnoreCase("")){
		for(int i = 0; i <quantity; i++){
		try {
			jo.put("ponum", ponum);// po单号
			jo.put("receivedunit", receivedunit);// 订购单位
			jo.put("issueunit", issueunit);// 发放单位
			jo.put("binnum", tobin);// 默认货位
			jo.put("itemnum", itemnum);// 物资编码
			jo.put("description", replaceSpecStr(description));// 物资长描述（物资名称、规格、型号）
			jo.put("udmatnum", udmatnum);// 原编码
			jo.put("actualdate", transdateStr);// 入库时间
			jo.put("udprintnum", 1);// 打印数量
			jo.put("id", invbalancesid);// 二维码
			jo.put("udapikey", udapikey);// 二维码		
			ja.put(jo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	}else if(!getString("udprintqty").equalsIgnoreCase("")){
			for(int i = 0; i <Integer.parseInt(getString("udprintqty")); i++){
				try {
					jo.put("ponum", ponum);// po单号
					jo.put("receivedunit", receivedunit);// 订购单位
					jo.put("issueunit", issueunit);// 发放单位
					jo.put("binnum", tobin);// 默认货位
					jo.put("itemnum", itemnum);// 物资编码
					jo.put("description", replaceSpecStr(description));// 物资长描述（物资名称、规格、型号）
					jo.put("udmatnum", udmatnum);// 原编码
					jo.put("actualdate", transdateStr);// 入库时间
					jo.put("udprintnum", 1);// 打印数量
					jo.put("id", invbalancesid);// 二维码
					jo.put("udapikey", udapikey);// 二维码				
					ja.put(jo);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String jsp_url = printUrl + ja.toString();
		clientSession.getCurrentApp().openURL(jsp_url, true);
	}
	return 1;
	}
	
	public String replaceSpecStr(String input) {
		if (input != null && !"".equals(input.trim())) {
			String regex = "[~`·；;：:？?，,、|。.!！@#￥$%^&*_+=《》！……&（）——“”''｛{}｝【】]";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(input);
			return matcher.replaceAll("");
		}
		return null;
	}
	
}
