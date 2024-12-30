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

public class SelWPWoreTaskDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote appMbo = this.app.getAppBean().getMbo();
		MboRemote mbo = this.getMbo();
		if (!mbo.isNull("worktype")) {
			String worktype = mbo.getString("worktype");
			String assetnum = mbo.getString("assetnum");
			MboSetRemote woTaskSet = mbo.getMboSet("UDWORETASKLINE");
			Vector<MboRemote> vector = woTaskSet.getSelection();
			int size = vector.size();
			if (size > 0) {
				Date plandate = appMbo.getDate("plandate");
				if (plandate == null) {
					plandate = MXServer.getMXServer().getDate();
				}
				// 设置第二天早上8点半
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime(plandate);
				calendar1.set(Calendar.HOUR_OF_DAY, 8);
				calendar1.set(Calendar.MINUTE, 30);
				calendar1.set(Calendar.SECOND, 0);
				Date targstartdate = calendar1.getTime();
				// 设置第二天早上8点半
				Calendar calendar2 = Calendar.getInstance();
				calendar2.setTime(plandate);
				calendar2.set(Calendar.HOUR_OF_DAY, 17);
				calendar2.set(Calendar.MINUTE, 30);
				calendar2.set(Calendar.SECOND, 0);
				Date targcompdate = calendar2.getTime();

				UDWOSet woSet = (UDWOSet) appMbo.getMboSet("$WORKORDER", "WORKORDER", "1=2");
				UDWO wo = (UDWO) woSet.add();
				wo.setValue("worktype", worktype, 2L);
				wo.setValue("assetnum", assetnum, 2L);
				wo.setValue("description", mbo.getString("asset.description") + "遗留问题", 11L);
				wo.setValue("targstartdate", targstartdate, 11L);
				wo.setValue("targcompdate", targcompdate, 11L);
				// String wonumnew = wo.getString("wonum");

				MboSetRemote woActivitySet = wo.getMboSet("UDGWOTASK");
				for (int i = 0; i < vector.size(); i++) {
					MboRemote mr = (MboRemote) vector.elementAt(i);
					MboRemote woActivity = woActivitySet.add();
					woActivity.setValue("content", mr.getString("vwodesc"), 11L);
					woActivity.setValue("mechname", mr.getString("wojo1"), 11L);
					woActivity.setValue("inspection", mr.getString("wojo2"), 11L);
					if (mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
						woActivity.setValue("result", "OK", 11L);
					} else {
						woActivity.setValue("result", "正常", 11L);
					}
					// mr.setValue("wonum", wonumnew, 11L);
					// mr.setValue("status", "ACTIVE", 11L);
					String woretasknum = mr.getString("woretasknum");
					woActivity.setValue("udworetasknum", woretasknum, 11L);
				}
			}
		}
		this.app.getAppBean().save();
		return 1;
	}
}
