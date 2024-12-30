package guide.webclient.beans.matsafe;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import guide.app.inventory.UDInvUseLine;
import guide.app.matsafe.MatSafe;
import guide.app.matsafe.MatSafeSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class InvUseLineMatSafeDateBean extends DataBean {

	/**
	 * 新建时将选择的进行复制
	 */
//	@Override
//	public int addrow() throws MXException {
//		try {
//			DataBean db = app.getDataBean("matsafeInfo_table");
//			MboRemote linembo = db.getMbo(db.getCurrentRow());// 选择行的信息
//			// 基础
//			String matsafetype = "", part = "", matsafedesc = "";
//			// 标准
//			double lockstd = 0, teustd = 0, unitstd = 0, actionstd = 0, runstd = 0, calstd = 0;
//			// 预警
//			double lockwarn = 0, teuwarn = 0, unitwarn = 0, actionwarn = 0, runwarn = 0, calwarn = 0;
//			// 实际
//			double lockact = 0;
//			if (linembo != null) {
//				matsafetype = linembo.getString("matsafetype");
//				part = linembo.getString("part");
//				matsafedesc = linembo.getString("matsafedesc");
//				// 标准
//				lockstd = linembo.getDouble("lockstd");
//				teustd = linembo.getDouble("teustd");
//				unitstd = linembo.getDouble("unitstd");
//				actionstd = linembo.getDouble("actionstd");
//				runstd = linembo.getDouble("runstd");
//				calstd = linembo.getDouble("calstd");
//				// 预警百分比
//				lockwarn = linembo.getDouble("lockwarn");
//				teuwarn = linembo.getDouble("teuwarn");
//				unitwarn = linembo.getDouble("unitwarn");
//				actionwarn = linembo.getDouble("actionwarn");
//				runwarn = linembo.getDouble("runwarn");
//				calwarn = linembo.getDouble("calwarn");
//				// 实际
//				lockact = linembo.getDouble("lockact");
//			}
//
//			super.addrow();
//			Date sysdate = MXServer.getMXServer().getDate();
//			MboRemote mbo = getMbo();
//			MboRemote owner = mbo.getOwner();
//			int invuselineid = owner.getInt("invuselineid");
//
//			mbo.setValue("invuselineid", invuselineid, 2L);
//			mbo.setValue("matsafetype", matsafetype, 11L);
//			mbo.setValue("part", part, 11L);
//			mbo.setValue("matsafedesc", matsafedesc, 11L);
//			mbo.setValue("upperdate", sysdate, 2L);
//
//			mbo.setValue("lockstd", lockstd, 11L);
//			mbo.setValue("teustd", teustd, 11L);
//			mbo.setValue("unitstd", unitstd, 11L);
//			mbo.setValue("actionstd", actionstd, 11L);
//			mbo.setValue("runstd", runstd, 11L);
//			mbo.setValue("calstd", calstd, 11L);
//
//			mbo.setValue("lockwarn", lockwarn, 11L);
//			mbo.setValue("teuwarn", teuwarn, 11L);
//			mbo.setValue("unitwarn", unitwarn, 11L);
//			mbo.setValue("actionwarn", actionwarn, 11L);
//			mbo.setValue("runwarn", runwarn, 11L);
//			mbo.setValue("calwarn", calwarn, 11L);
//
//			mbo.setValue("lockact", lockact, 11L);
//
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		} catch (MXException e) {
//			e.printStackTrace();
//		}
//		return 1;
//	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean table = app.getDataBean("matsafe_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote invuseLine = table.getParent().getMbo();
		Date sysdate = MXServer.getMXServer().getDate();
		if (invuseLine != null && invuseLine instanceof UDInvUseLine) {
			MatSafeSet lineSet = (MatSafeSet) invuseLine.getMboSet("MATSAFERUN");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MatSafe line = (MatSafe) lineSet.add();
				int invuselineid = invuseLine.getInt("invuselineid");
				String matsafetype = mr.getString("matsafetype");
				String part = mr.getString("part");
				String matsafedesc = mr.getString("matsafedesc");
				// 标准
				double lockstd = mr.getDouble("lockstd");
				double teustd = mr.getDouble("teustd");
				double unitstd = mr.getDouble("unitstd");
				double actionstd = mr.getDouble("actionstd");
				double runstd = mr.getDouble("runstd");
				double calstd = mr.getDouble("calstd");
				// 预警百分比
				double lockwarn = mr.getDouble("lockwarn");
				double teuwarn = mr.getDouble("teuwarn");
				double unitwarn = mr.getDouble("unitwarn");
				double actionwarn = mr.getDouble("actionwarn");
				double runwarn = mr.getDouble("runwarn");
				double calwarn = mr.getDouble("calwarn");
				// 实际
				double lockact = mr.getDouble("lockact");

				line.setValue("invuselineid", invuselineid, 2L);
				line.setValue("matsafetype", matsafetype, 11L);
				line.setValue("part", part, 11L);
				line.setValue("matsafedesc", matsafedesc, 11L);
				line.setValue("upperdate", sysdate, 2L);

				line.setValue("lockstd", lockstd, 11L);
				line.setValue("teustd", teustd, 11L);
				line.setValue("unitstd", unitstd, 11L);
				line.setValue("actionstd", actionstd, 11L);
				line.setValue("runstd", runstd, 11L);
				line.setValue("calstd", calstd, 11L);

				line.setValue("lockwarn", lockwarn, 11L);
				line.setValue("teuwarn", teuwarn, 11L);
				line.setValue("unitwarn", unitwarn, 11L);
				line.setValue("actionwarn", actionwarn, 11L);
				line.setValue("runwarn", runwarn, 11L);
				line.setValue("calwarn", calwarn, 11L);

				line.setValue("lockact", lockact, 11L);
			}
		}
		return super.execute();
	}
}
