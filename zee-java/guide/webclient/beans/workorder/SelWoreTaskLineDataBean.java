package guide.webclient.beans.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import guide.app.workorder.UDWO;
import guide.app.workorder.UDWOSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWoreTaskLineDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		mbo.validate();
		MboSetRemote vwoMainSet = mbo.getMboSet("UDVWOREMAIN");
		if (vwoMainSet != null && !vwoMainSet.isEmpty()) {
			MboRemote vwoMain = vwoMainSet.getMbo(0);
			if (!vwoMain.isNull("worktype")) {
				String worktype = vwoMain.getString("worktype");// 工单类型
				String assetnum = vwoMain.getString("assetnum");// 设备编号
				MboSetRemote woTaskSet = this.getMbo().getMboSet("UDWORETASKLINE");
				Vector<MboRemote> vector = woTaskSet.getSelection();
				int size = vector.size();
				if (size > 0) {
					String udcompany = mbo.getString("udcompany");
					UDWOSet woSet = (UDWOSet) mbo.getMboSet("$WORKORDER", "WORKORDER", "1=2");
					Date targstartdate = MXServer.getMXServer().getDate();
					UDWO wo = (UDWO) woSet.add();
					wo.setValue("worktype", worktype, 2L);
					wo.setValue("assetnum", assetnum, 2L);
					wo.setValue("targstartdate", targstartdate, 11L);
					if ("AE03ADT".equalsIgnoreCase(udcompany) || "GR02PCT".equalsIgnoreCase(udcompany)) {
						wo.setValue("description", vwoMain.getString("asset.description") + " Remain item", 11L);
						wo.setValue("status", "INPRG", 11L);
					} else {
						wo.setValue("description", vwoMain.getString("asset.description") + " 遗留问题", 11L);
					}
					MboSetRemote woActivitySet = wo.getMboSet("UDGWOTASK");
					for (int i = 0; i < vector.size(); i++) {
						MboRemote mr = (MboRemote) vector.elementAt(i);
						MboRemote woActivity = woActivitySet.add();
						woActivity.setValue("content", mr.getString("wodesc"), 11L);
						woActivity.setValue("mechname", mr.getString("wojo1"), 11L);
						woActivity.setValue("inspection", mr.getString("wojo2"), 11L);
						if (mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
							woActivity.setValue("result", "OK", 11L);
						} else {
							woActivity.setValue("result", "正常", 11L);
						}
						if ("AE03ADT".equalsIgnoreCase(udcompany) || "GR02PCT".equalsIgnoreCase(udcompany)) {
							mr.setValue("status", "E", 2L);
						}
						woActivity.setValue("udworetasknum", mr.getString("woretasknum"), 11L);
					}
				}
			}
		}
		return super.execute();
	}
}
