package guide.app.workplan;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class WorkPlan extends UDMbo implements MboRemote {

	public WorkPlan(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		Date date = MXServer.getMXServer().getDate();
		Date nextDay = CommonUtil.getCalDate(date, 1);
		setValue("plandate", nextDay, 11L);
		setValue("description", CommonUtil.getDateFormat(nextDay, "yyyy-MM-dd") + "工作计划", 11L);
		setValue("apprby", getUserInfo().getPersonId(), 11L);

		// 找到上一次的每日计划
		MboSetRemote workPlanSet = this.getMboSet("$UDWORKPLAN", "UDWORKPLAN",
				"udcompany=:udcompany and uddept=:uddept and udofs=:udofs and nvl(udcrew,'NA')=nvl(:udcrew,'NA')");
		workPlanSet.setOrderBy("udworkplanid desc");
		workPlanSet.reset();
		if (!workPlanSet.isEmpty() && workPlanSet.count() > 0) {
			MboRemote workPlan = workPlanSet.getMbo(0);
			getMboValue("plannum").autoKey();
			String plannum = getString("plannum");
			// 员工
			MboSetRemote workLaborSet1 = workPlan.getMboSet("UDWORKLABOR");
			if (!workLaborSet1.isEmpty() && workLaborSet1.count() > 0) {
				MboSetRemote workLaborSet2 = this.getMboSet("UDWORKLABOR");
				for (int i = 0; workLaborSet1.getMbo(i) != null; i++) {
					MboRemote workLabor2 = workLaborSet2.addAtEnd();
					MboRemote workLabor1 = workLaborSet1.getMbo(i);
					String personid = workLabor1.getString("personid");
					String description = workLabor1.getString("description");
					workLabor2.setValue("plannum", plannum, 11L);
					workLabor2.setValue("personid", personid, 11L);
					workLabor2.setValue("description", description, 11L);
					workLabor2.setValue("status", "Y", 11L);
				}
			}
			// 安全隐患
			MboSetRemote workHazardSet1 = workPlan.getMboSet("UDWORKHAZARD");
			if (!workHazardSet1.isEmpty() && workHazardSet1.count() > 0) {
				MboSetRemote workHazardSet2 = this.getMboSet("UDWORKHAZARD");
				for (int i = 0; workHazardSet1.getMbo(i) != null; i++) {
					MboRemote workHazard2 = workHazardSet2.addAtEnd();
					MboRemote workHazard1 = workHazardSet1.getMbo(i);
					String hiddannum = workHazard1.getString("hiddannum");
					String risk = workHazard1.getString("risk");
					String description = workHazard1.getString("description");
					workHazard2.setValue("plannum", plannum, 11L);
					workHazard2.setValue("hiddannum", hiddannum, 11L);
					workHazard2.setValue("risk", risk, 11L);
					workHazard2.setValue("description", description, 11L);
				}
			}
		}
	}
}
