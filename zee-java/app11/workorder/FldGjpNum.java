package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldGjpNum extends MAXTableDomain {

	public FldGjpNum(MboValue mbv) throws MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDGJOBPLAN", "gjpnum=:" + thisAttr);
		String[] FromStr = { "gjpnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String sql = "status = 'ACTIVE' and udcompany = :udcompany and uddept = :uddept and assettype = :udassettypecode and worktype = :worktype";
		if (!mbo.isNull("udofs")) {
			sql += " and udofs = :udofs";
		}
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		MboRemote mbo = this.getMboValue().getMbo();
		String wonum = mbo.getString("wonum");
		MboSetRemote gjobPlanSet = mbo.getMboSet("UDGJOBPLAN");
		if (gjobPlanSet != null && !gjobPlanSet.isEmpty()) {
			MboRemote gjobPlan = gjobPlanSet.getMbo(0);
			String woWorkType = mbo.getString("worktype");
			if (woWorkType == null || woWorkType.equalsIgnoreCase("")) {
				mbo.setValue("worktype", gjobPlan.getString("worktype"), 11L);
			}
			String description = mbo.getString("description");
			if (description == null || description.equalsIgnoreCase("")) {
				mbo.setValue("description", gjobPlan.getString("description"), 11L);
			}
			mbo.setValue("udnotshutdown", gjobPlan.getBoolean("nonstop"), 11L);// 不停机
			mbo.setValue("udworktype1", gjobPlan.getString("udworktype1"), 11L);// 维保类型

			// 作业任务
			MboSetRemote gjobTaskSet = gjobPlan.getMboSet("UDGJOBTASK");
			if (!gjobTaskSet.isEmpty() && gjobTaskSet.count() > 0) {
				MboSetRemote gwoTaskSet = mbo.getMboSet("UDGWOTASK");
				if (!gwoTaskSet.isEmpty() && gwoTaskSet.count() > 0) {
					gwoTaskSet.deleteAll(11L);
				}
				for (int i = 0; gjobTaskSet.getMbo(i) != null; i++) {
					MboRemote gjobTask = gjobTaskSet.getMbo(i);
					MboRemote gwoTask = gwoTaskSet.add();
					gwoTask.setValue("wonum", wonum, 11L);
					gwoTask.setValue("linenum", gjobTask.getString("linenum"), 11L);
					gwoTask.setValue("mechname", gjobTask.getString("mechname"), 11L);
					gwoTask.setValue("content", gjobTask.getString("content"), 11L);
					gwoTask.setValue("inspection", gjobTask.getString("inspection"), 11L);
					gwoTask.setValue("jpduration", gjobTask.getDouble("jpduration"), 11L);
					gwoTask.setValue("result", gjobTask.getString("result"), 11L);
				}
			}

			// 作业人员
			MboSetRemote gjobLaborSet = gjobPlan.getMboSet("UDGJOBLABOR");
			if (!gjobLaborSet.isEmpty() && gjobLaborSet.count() > 0) {
				MboSetRemote wplaborSet = mbo.getMboSet("UDWPLABOR");
				if (wplaborSet.isEmpty()) {
					for (int i = 0; gjobLaborSet.getMbo(i) != null; i++) {
						MboRemote gjobLabor = gjobLaborSet.getMbo(i);
						MboRemote wplabor = wplaborSet.add();
						wplabor.setValue("wonum", wonum, 11L);
						wplabor.setValue("laborcode", gjobLabor.getString("laborcode"), 11L);
						wplabor.setValue("islead", gjobLabor.getBoolean("islead"), 11L);
					}
				}
			}

			// 安全隐患
			MboSetRemote gjobHiddanSet = gjobPlan.getMboSet("UDGJOBHIDDAN");
			if (!gjobHiddanSet.isEmpty() && gjobHiddanSet.count() > 0) {
				MboSetRemote gwoHiddanSet = mbo.getMboSet("UDWOHIDDAN");
				if (!gwoHiddanSet.isEmpty() && gwoHiddanSet.count() > 0) {
					gwoHiddanSet.deleteAll(11L);
				}
				for (int i = 0; gjobHiddanSet.getMbo(i) != null; i++) {
					MboRemote gjobHiddan = gjobHiddanSet.getMbo(i);
					MboRemote gwoHiddan = gwoHiddanSet.add();
					gwoHiddan.setValue("wonum", wonum, 11L);
					gwoHiddan.setValue("udhiddannum", gjobHiddan.getString("udhiddannum"), 11L);
					gwoHiddan.setValue("risk", gjobHiddan.getString("risk"), 11L);
					gwoHiddan.setValue("description", gjobHiddan.getString("description"), 11L);
				}
			}
		}
	}

}
