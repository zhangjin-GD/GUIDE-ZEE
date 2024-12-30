package guide.webclient.beans.pr;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.webclient.system.runtime.WebClientRuntime;

public class SelPRNotRFQDateBean extends DataBean {

	public synchronized int autoAlloc() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		MboSetRemote prlineSet = mbo.getMboSet("PRLINE");
		if (!prlineSet.isEmpty() && prlineSet.count() > 0) {
			for (int i = 0; prlineSet.getMbo(i) != null; i++) {
				MboRemote prline = prlineSet.getMbo(i);
				String purchaser = prline.getString("uditemcp.purchaser");
				if (purchaser != null && !purchaser.isEmpty()) {
					prline.setValue("udpurchaser", purchaser, 11L);
				}
			}
		}
		this.app.getAppBean().save();
		WebClientRuntime.sendEvent(new WebClientEvent("dialogclose", app.getCurrentPageId(), null, clientSession));// 关闭窗口
		return 1;
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		String beanId = this.getId();
		MboRemote mbo = this.app.getAppBean().getMbo();

		MboSetRemote vprSet = mbo.getMboSet("UDVPR");
		if (vprSet != null && !vprSet.isEmpty()) {
			MboRemote vpr = vprSet.getMbo(0);
			if (!vpr.isNull("udpurchaser")) {
				if ("udprnotrfq".equalsIgnoreCase(beanId)) {
					getLineSelection(mbo, vpr, "PRNOTRFQ", "PRLINE");// PO应用
				} else if ("udplnotrfq".equalsIgnoreCase(beanId)) {
					if (!vpr.isNull("reason")) {
						getLineSelection(mbo, vpr, "PLNOTRFQ", "UDPRNOTRFQ");// POLINE应用 对行
					} else {
						throw new MXApplicationException("guide", "1092");
					}
				} else if ("udplnotrfqnotpur".equalsIgnoreCase(beanId)) {
					getLineSelection(mbo, vpr, "PLNOTRFQNOTPUR", "UDPRNOTRFQ");// POLINE应用 对行 采购员为空
				} else if ("udplnotrfqid".equalsIgnoreCase(beanId)) {
					if (!vpr.isNull("reason")) {
						getLineSelection(mbo, vpr, "PLNOTRFQID", "UDPRNOTRFQ");// POLINE应用 单行
					} else {
						throw new MXApplicationException("guide", "1092");
					}
				}
			} else {
				throw new MXApplicationException("guide", "1017");
			}
		}
		this.app.getAppBean().save();
		return 1;
	}

	private void getLineSelection(MboRemote mbo, MboRemote vpr, String relationship1, String relationship2)
			throws RemoteException, MXException {
		MboSetRemote prLineSet = this.getMbo().getMboSet(relationship1);
		if ("PLNOTRFQID".equalsIgnoreCase(relationship1)) {
			MboRemote mr = prLineSet.getMbo(0);
			setPrlinePurchaser(mbo, vpr, mr, relationship2);
		} else {
			Vector<MboRemote> vector = prLineSet.getSelection();
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				setPrlinePurchaser(mbo, vpr, mr, relationship2);
			}
		}
	}

	private void setPrlinePurchaser(MboRemote mbo, MboRemote vpr, MboRemote mr, String relationship)
			throws RemoteException, MXException {
		int vprlineid = mr.getInt("prlineid");
		String oldValue = mr.getString("udpurchaser");
		String personId = mbo.getUserInfo().getPersonId();
		String newValue = vpr.getString("udpurchaser");
		String reason = vpr.getString("reason");
		Date currentDate = MXServer.getMXServer().getDate();
		MboSetRemote prlineSet = mbo.getMboSet(relationship);
		prlineSet.setWhere("prlineid ='" + vprlineid + "'");
		prlineSet.reset();
		if (!prlineSet.isEmpty() && prlineSet.count() > 0) {
			MboRemote prline = prlineSet.getMbo(0);
			prline.setValue("udpurchaser", newValue, 11L);
			prline.setValue("udpurchasertime", currentDate, 11L);

			if (!oldValue.isEmpty() && !newValue.isEmpty() && !reason.isEmpty()) {
				MboSetRemote changeHisSet = prline.getMboSet("UDCHANGEHIS");
				MboRemote changeHis = changeHisSet.add(11L);
				changeHis.setValue("ownerid", vprlineid, 11L);
				changeHis.setValue("ownertable", "PRLINE", 11L);
				changeHis.setValue("attributename", "UDPURCHASER", 11L);
				changeHis.setValue("oldvalue", oldValue, 11L);
				changeHis.setValue("newvalue", newValue, 11L);
				changeHis.setValue("reason", reason, 11L);
				changeHis.setValue("changeby", personId, 11L);
				changeHis.setValue("changedate", currentDate, 11L);
			}
			prline.getThisMboSet().save();
		}
	}

}
