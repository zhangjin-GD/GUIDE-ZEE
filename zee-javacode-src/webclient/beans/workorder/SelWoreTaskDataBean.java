package guide.webclient.beans.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import guide.app.common.CommonUtil;
import guide.app.workorder.UDWO;
import guide.app.workorder.UDWOSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWoreTaskDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		MboSetRemote vwoMainSet = mbo.getMboSet("UDVWOREMAIN");
		if (vwoMainSet != null && !vwoMainSet.isEmpty()) {
			MboRemote vwoMain = vwoMainSet.getMbo(0);
			if (!vwoMain.isNull("worktype")) {
				String worktype = vwoMain.getString("worktype");
				MboSetRemote woTaskSet = this.getMbo().getMboSet("UDWORETASK");
				Vector<MboRemote> vector = woTaskSet.getSelection();
				int size = vector.size();
				if (size > 0) {
					String oriwonum = "";
					String oriwonums = "";
					if (!mbo.isNull("wonum")) {
						oriwonum = mbo.getString("wonum");
						oriwonums = CommonUtil.autoKeyNum("WORKORDER", "UDORIWONUMS", oriwonum + "-", "", 2);
					}
					UDWOSet woSet = (UDWOSet) mbo.getMboSet("$WORKORDER", "WORKORDER", "1=2");
					Date targstartdate = MXServer.getMXServer().getDate();
					if (!mbo.isNull("planstartdate")) {
						targstartdate = mbo.getDate("planstartdate");
					}
					UDWO wo = (UDWO) woSet.add();
					wo.setValue("worktype", worktype, 2L);
					wo.setValue("assetnum", mbo.getString("assetnum"), 2L);
					wo.setValue("description", mbo.getString("asset.description") + "遗留问题", 11L);
					wo.setValue("targstartdate", targstartdate, 11L);
					wo.setValue("targcompdate", mbo.getDate("planenddate"), 11L);
					wo.setValue("udoriwonum", oriwonum, 11L);
					wo.setValue("udoriwonums", oriwonums, 11L);
					String wonumnew = wo.getString("wonum");

					MboSetRemote woActivitySet = wo.getMboSet("UDGWOTASK");
					for (int i = 0; i < vector.size(); i++) {
						MboRemote mr = (MboRemote) vector.elementAt(i);
						MboRemote woActivity = woActivitySet.add();
						woActivity.setValue("linenum", mr.getInt("taskid"), 11L);
						woActivity.setValue("content", mr.getString("wodesc"), 11L);
						woActivity.setValue("mechname", mr.getString("wojo1"), 11L);
						woActivity.setValue("inspection", mr.getString("wojo2"), 11L);
						if (mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
							woActivity.setValue("result", "OK", 11L);
						} else {
							woActivity.setValue("result", "正常", 11L);
						}
						mr.setValue("wonum", wonumnew, 11L);
						mr.setValue("status", "ACTIVE", 2L);
					}
				}
			}
		}
		return super.execute();
	}
}
