package guide.webclient.beans.po;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.po.POLineTableBean;
import psdi.webclient.system.beans.DataBean;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import guide.app.common.CommonUtil;
import guide.app.po.UDPO;

public class UDPOLineTableBean extends POLineTableBean {

	public int selaltitem() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		String status = owner.getString("status");
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}

		if ("APPR".equalsIgnoreCase(status) || "CLOSE".equalsIgnoreCase(status)) {
			throw new MXApplicationException("guide", "1066");
		}
		// 调用dialog
		this.clientSession.loadDialog("selaltitem");
		return 1;
	}

	public int empty() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		String status = owner.getString("status");
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}

		if (!"WAPPR".equalsIgnoreCase(status) && !"BACK".equalsIgnoreCase(status)) {
			throw new MXApplicationException("guide", "1066");
		}
		MboSetRemote polineSet = owner.getMboSet("poline");
		if (!polineSet.isEmpty() && polineSet.count() > 0) {
			for (int i = 0; polineSet.getMbo(i) != null; i++) {
				MboRemote poline = polineSet.getMbo(i);
				poline.setValue("udtotalprice", 0, 2L);
				poline.setValue("udpredicttaxprice", 0, 2L);
			}
		}
		this.refreshTable();
		return 1;
	}

	// pr 重新编号
	public int cxpx() throws RemoteException, MXException {
		int j = 1;
		MboRemote mbo = app.getAppBean().getMbo();
		MboSetRemote lineSet = mbo.getMboSet("POLINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			for (int i = 0; lineSet.getMbo(i) != null; i++) {
				MboRemote line = lineSet.getMbo(i);
				line.setValue("polinenum", j++, 2L);
			}
		}
		this.refreshTable();
		return 1;
	}

	public int polineSendVendor() throws MXException, RemoteException, JSONException {
		MboRemote mbo = this.parent.getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		String flag = "Tip，there are no purchase order details lines to send!";
		DataBean db = app.getDataBean("polines_poline_table");
		MboRemote linembo = db.getMbo(db.getCurrentRow());
		if (linembo != null) {
			MboSetRemote polineSet = linembo.getMboSet("UDPONUM");
			if (!polineSet.isEmpty() && polineSet.count() > 0) {
				String ponum = mbo.getString("ponum");//订单号
				String purchasename = mbo.getString("PURCHASE.displayname");//采购员
				double totaltax1 = mbo.getDouble("totaltax1");//税额
				double totalcost = mbo.getDouble("totalcost");//含税金额
				double pretaxtotal = totalcost-totaltax1;//不含税金额
				String toAddress = CommonUtil.getValue(mbo, "VENDOR", "udemail");
				String title = "PO：" + ponum + "，Purchaser：" + purchasename+ "，Amount including tax："+totalcost+"，Amount excluding tax："+pretaxtotal+"，Tax amount："+totaltax1;
				if (toAddress != null && !toAddress.equalsIgnoreCase("")) {
					// 报表参数
					JSONObject paramRpt = new JSONObject();
					paramRpt.put("reportName", "udpomatl_djzee.rptdesign");
					paramRpt.put("description", "POZEE-includingtax");
					paramRpt.put("appName", "UDPOZEE");
					paramRpt.put("keyNum", ponum);
					JSONObject paramData = new JSONObject();
					paramData.put("recnum", ponum);
					// 报表执行
					File attachment = CommonUtil.getReport(clientSession.getUserInfo(), paramRpt, paramData);
					// 消息参数
					String personId = mbo.getUserInfo().getPersonId();
					String currentDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");
					JSONObject jsonData = new JSONObject();
					jsonData.put("id", ponum);
					jsonData.put("to_user", toAddress);
					jsonData.put("subject", title);
					jsonData.put("content", title);
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
						for (int i = 0; db.getMbo(i) != null; i++) {
							MboRemote polien = db.getMbo(i);
							polien.setValue("udstatus", "SENT",11L);
						}
						polineSet.close();
						mbo.setValue("udposent", MXServer.getMXServer().getDate(), 11L);
						this.app.getAppBean().save();
						
						flag = "PO" + ponum + "The email has been successfully sent!";
					} else {
						flag = "PO" + ponum + "Email sending failed:"
								+ CommonUtil.getString(new JSONObject(returnResult), "result");
					}
				} else {
					flag = "Tip, no recipient information, no email sent!";
				}
			}
		}
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", flag, 1);
		return 1;
	}
}
