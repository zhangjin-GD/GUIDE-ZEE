package guide.webclient.beans.po;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.StatefulMboRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.po.POAppBean;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.WebClientEvent;

public class UDPOAppBean extends POAppBean {

	public int UDCAN() throws RemoteException, MXException {
		// 是否流程中
		MboRemote mbo = this.getMbo();
		MboSetRemote wfassignmentSet = mbo.getMboSet("WFASSIGNMENT");
		if (!wfassignmentSet.isEmpty() && wfassignmentSet.count() > 0) {
			throw new MXApplicationException("guide", "1020");
		}
		DataBean appBean = this.app.getAppBean();
		StatefulMboRemote stateful = (StatefulMboRemote) appBean.getMbo();
		this.targetStatusOption = "CAN";
		this.statusChangeButtonSigoption = "CAN";
		stateful.setTargetStatusOption("CAN");
		stateful.setStatusChangeButtonSigoption("CAN");
		this.STATUSSUB();
		if (this.app.onListTab()) {
			this.clientSession.loadDialog("list_status");
		} else {
			this.clientSession.loadDialog("status");
		}
		return 1;
	}

	public int UDREVPO() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		// 状态
		String status = owner.getString("status");
		if (!"APPR".equalsIgnoreCase(status)) {
			throw new MXApplicationException("guide", "1066");
		}
		// 是否接收
		String receipts = owner.getString("RECEIPTS");
		if (!"NONE".equalsIgnoreCase(receipts)) {
			throw new MXApplicationException("guide", "1123");
		}
		this.clientSession.loadDialog("udrevpo");
		return 1;

	}

	// 字段 已订购 状态 时 ， 算出 交货时间
	public int Ordered() throws RemoteException, MXException {
		MboRemote mbo = app.getAppBean().getMbo();
		String status = mbo.getString("status");
		if ("APPR".equalsIgnoreCase(status)) {
			mbo.setValue("UDSFDG", "ORDERED", 11L);
			MboSetRemote mboSet = mbo.getMboSet("poline");
			if (!mboSet.isEmpty() && mboSet.count() > 0) {
				for (int i = 0; i < mboSet.count(); i++) {
					MboRemote mbo1 = mboSet.getMbo(i);
					// 校验供货天不为空
					if (mbo1.getString("UDDELIVERYTIME") != null && mbo1.getString("UDDELIVERYTIME") != "") {
						Date date = MXServer.getMXServer().getDate();
						Calendar calendar = new GregorianCalendar();
						calendar.setTime(date);
						calendar.add(Calendar.DATE, Integer.parseInt(mbo1.getString("UDDELIVERYTIME")));
						mbo1.setValue("UDDELIVERYDATE", calendar.getTime(), 11L);
					}
				}
			}
		}
		app.getAppBean().save();
		return 1;
	}
	
	public int RUNAREPORT() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		String udcompany = mbo.getString("udcompany");
		if (udcompany != null && udcompany.equalsIgnoreCase("ZEE")) {
			String rptNum = "";
			WebClientEvent event = this.clientSession.getCurrentEvent();
			Object eventValue = event.getValue();
			if ((eventValue instanceof Hashtable)) {
				Hashtable evtHash = (Hashtable) eventValue;
				rptNum = evtHash.get("reportnumber").toString();
			} else {
				rptNum = event.getValue().toString();
			}
			rptNum = rptNum.replace(",", "");
			if (rptNum != null && rptNum.contains("1599")) { //udpomatl_djzeedjfinal.rptdesign
				mbo.setValue("udfinaldate", MXServer.getMXServer().getDate(), 11L);
				mbo.setValue("udposent", MXServer.getMXServer().getDate(), 11L);
				mbo.getThisMboSet().save();
				MboSetRemote polineSet = mbo.getMboSet("POLINE");
				if (!polineSet.isEmpty() && polineSet.count() > 0) {
					for (int i = 0; i < polineSet.count(); i++) {
						MboRemote poline = polineSet.getMbo(i);
						poline.setValue("udstatus", "SENT", 11L);
					}
					polineSet.save();
				}
				this.app.getAppBean().refreshTable();
				this.app.getAppBean().reloadTable();
			}
		}
		return super.RUNAREPORT();
	}
	
}
