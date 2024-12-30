package guide.webclient.beans.workorder;

import guide.app.common.CommonUtil;
import guide.app.workorder.UDWO;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.workorder.WorkorderAppBean;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.webclient.system.session.WebClientSession;

import guide.app.pr.UDPR;

public class UDWorkOrderAppBean extends WorkorderAppBean {

	public int CREATEPRMAT() throws RemoteException, MXException {
		UDWO wo = (UDWO) this.getMbo();
		MboSetRemote gjobMatSet = wo.getMboSet("UDGJOBMATERIAL");
		if (!gjobMatSet.isEmpty() && gjobMatSet.count() > 0) {
			String prnum = wo.createPrMat();
			String params = "单号：" + prnum + "！/No.:" + prnum;
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "温馨提示/Reminder", params, 1);
		} else {
			throw new MXApplicationException("guide", "1107");
		}
		this.app.getAppBean().save();
		return 1;
	}

	public void worhang() throws RemoteException, MXException {
		MboRemote mbo = this.getMbo();
		String status = mbo.getString("udwostatus");
		if (!status.equalsIgnoreCase("HANG")) {
			Object[] obj = { "未挂起！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}
	}

	public void wohang() throws RemoteException, MXException {
		UDWO mbo = (UDWO) this.getMbo();
		String status = mbo.getString("udwostatus");
		if (status.equalsIgnoreCase("END") || status.equalsIgnoreCase("HANG")
				|| mbo.getInternalStatus().equalsIgnoreCase("APPR")) {
			Object[] obj = { "当前状态无法挂起！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}
	}

	public void createCmWork() throws RemoteException, MXException {
		UDWO mbo = (UDWO) this.getMbo();
		String udwoanalysis = mbo.getString("udwoanalysis");
		MboSetRemote oriWoSet = mbo.getMboSet("UDORIWO");
		if (oriWoSet != null && !oriWoSet.isEmpty()) {
			MboRemote oriWo = oriWoSet.getMbo(0);
			String wonum = oriWo.getString("wonum");
			Object[] obj = { wonum };
			throw new MXApplicationException("guide", "1045", obj);
		}
		if (!"1FAULT".equalsIgnoreCase(udwoanalysis) || mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1065");
		}
	}

	public int emConfirm() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		if (mbo.getBoolean("udconfirm")) {
			Object params[] = { "提示：已回传至故障提报系统，请勿重复操作！" };
			throw new MXApplicationException("instantmessaging", "tsdimexception", params);
		}
		String flag = "提示，您所在的公司未启用！";
		if (mbo.getString("udcompany").equalsIgnoreCase("3120GOCT")) {
			flag = ((UDWO) mbo).emGoctConfirm();
			System.out.println("\n-------------------" + flag);
			try {
				flag = CommonUtil.getString(new JSONObject(flag), "code");
			} catch (JSONException e) {
				flag = e.toString();
				e.printStackTrace();
			}
			if (flag.equalsIgnoreCase("success") || flag.equalsIgnoreCase("200")) {
				flag = "提示，已回传至故障提报系统！";
				mbo.setValue("udconfirm", 1, 11L);
			}
		}
		this.app.getAppBean().save();
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", flag, 1);
		return 1;
	}

	public int udupper() throws RemoteException, MXException {
		UDWO mbo = (UDWO) this.getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		// 调用dialog
		this.clientSession.loadDialog("udupper");
		return 1;
	}

	public int udlower() throws RemoteException, MXException {
		UDWO mbo = (UDWO) this.getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		// 调用dialog
		this.clientSession.loadDialog("udlower");
		return 1;
	}

	public int setValueStatus() throws RemoteException, MXException {
		MboRemote mbo = app.getAppBean().getMbo();
		WebClientEvent event = clientSession.getCurrentEvent();
		String eventValue = event.getValue().toString();
		if (eventValue == null || eventValue.equalsIgnoreCase("")) {
			eventValue = "WAPPR";
		}
		mbo.setValue("status", eventValue, 11L);
		app.getAppBean().save();
		return 1;
	}
	
	/*
	 * ZEE工单维修备件直接创建PO
	 * DJY 2024-3-21-10:00
	 */
	public void udcreatPR() throws RemoteException, MXException {
		Date sysdate = MXServer.getMXServer().getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sysdateStr = sdf.format(sysdate);
		MboRemote wo = this.app.getAppBean().getMbo();
		MboSetRemote newprSet = MXServer.getMXServer().getMboSet("PR",
				MXServer.getMXServer().getSystemUserInfo());
		newprSet.setWhere(" 1=2 ");
		MboRemote newpr = (UDPR) newprSet.add();
		String prnum = newpr.getString("prnum");
		newpr.setValue("udapptype", "PRZEE", 11L);
		newpr.setValue("exchangedate", sysdate, 11L);
		newpr.setValue("requireddate", sysdate, 11L);
		newpr.setValue("requestedby", wo.getString("reportedby"), 11L);// 创建人
		newpr.setValue("udcreatetime", sysdate, 11L);// 创建时间
		newpr.setValue("udcompany", wo.getString("udcompany"), 11L);
		newpr.setValue("uddept", wo.getString("uddept"), 11L);
		newprSet.save();
		clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "",
				"Result： Create a PR for repair, PR number is :  " + prnum
						+ " !", 1);
		String appname = "UDPRZEE";
		WebClientEvent event = this.clientSession.getCurrentEvent();
		if (event != null) {
			String value = event.getValueString();
			if (value != null) {
				if (value.equals("")) {
					super.execute();
					// 获取系统session实例
					WebClientSession wcs = sessionContext.getMasterInstance();
					// 构建跳转至启动中心的URL
					String url = "?event=loadapp&value=" + appname
							+ "&uniqueid=" + newpr.getInt("PRID") + "";
					// 跳转动作执行
					wcs.gotoApplink(url);
				}
			}
		}
		newprSet.close();
	}
}
