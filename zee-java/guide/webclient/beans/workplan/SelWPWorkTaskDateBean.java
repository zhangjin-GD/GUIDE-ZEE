package guide.webclient.beans.workplan;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import guide.app.workorder.UDWO;
import guide.app.workorder.UDWOSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWPWorkTaskDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		workTask();
		return super.execute();
	}

	private void workTask() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		MboSetRemote vwoMainSet = mbo.getMboSet("UDVWOREMAIN");
		if (vwoMainSet != null && !vwoMainSet.isEmpty()) {
			MboRemote vwoMain = vwoMainSet.getMbo(0);
			if (!vwoMain.isNull("worktype") && !vwoMain.isNull("description")) {
				String worktype = vwoMain.getString("worktype");
				String description = vwoMain.getString("description");
				MboSetRemote woTaskSet = mbo.getMboSet("UDWORKTASK");
				Vector<MboRemote> vector = woTaskSet.getSelection();
				int size = vector.size();
				if (size > 0) {
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

					MboRemote thisMR = (MboRemote) vector.elementAt(0);
					String assetnum = thisMR.getString("assetnum");
					UDWOSet woSet = (UDWOSet) mbo.getMboSet("$WORKORDER", "WORKORDER", "1=2");
					UDWO wo = (UDWO) woSet.add();
					wo.setValue("worktype", worktype, 2L);
					wo.setValue("assetnum", assetnum, 2L);
					wo.setValue("description", description, 11L);
					wo.setValue("targstartdate", targstartdate, 11L);
					wo.setValue("targcompdate", targcompdate, 11L);
					String wonumnew = wo.getString("wonum");

					MboSetRemote woActivitySet = wo.getMboSet("UDGWOTASK");
					for (int i = 0; i < vector.size(); i++) {
						MboRemote mr = (MboRemote) vector.elementAt(i);
						MboRemote woActivity = woActivitySet.add();
						woActivity.setValue("content", mr.getString("workcontent"), 11L);
						if (mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
							woActivity.setValue("result", "OK", 11L);
						} else {
							woActivity.setValue("result", "正常", 11L);
						}
						mr.setValue("wonum", wonumnew, 11L);
					}
				}
			}
		}
	}
}
