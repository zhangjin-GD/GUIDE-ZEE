package guide.webclient.beans.workorder;

import java.rmi.RemoteException;

import guide.app.woremain.WoreTask;
import guide.app.woremain.WoreTaskSet;
import guide.app.workorder.UDWO;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class WorkMainTableBean extends DataBean {

	@Override
	public int addrow() throws MXException {
		int add = super.addrow();
		try {
			MboRemote mbo = this.getMbo();
			MboRemote owner = this.app.getAppBean().getMbo();
			if (owner != null) {
				WoreTaskSet woRetaskSet = (WoreTaskSet) mbo.getMboSet("UDWORETASK");
				WoreTask woRetask = (WoreTask) woRetaskSet.add();
				if (owner instanceof UDWO) {
					String worktype = owner.getString("worktype");
					if ("EM".equalsIgnoreCase(worktype)) {
						woRetask.setValue("WOJO1", owner.getString("udfailmechdesc"), 11L);
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return add;
	}

	/**
	 * 创建工单
	 * 
	 * @throws MXException
	 * @throws RemoteException
	 */
	public void createWork() throws RemoteException, MXException {
		MboSetRemote workSet = this.getParent().getMboSet();
		MboSetRemote mboSet = this.getMboSet();
		for (int i = 0; i < mboSet.count(); i++) {
			MboRemote mbo = mboSet.getMbo(i);
			MboRemote work = workSet.add();
			mbo.setValue("solvewonum", work.getString("wonum"), 11L);
			work.setValue("assetnum", mbo.getString("assetnum"), 11L);
			work.setValue("worktype", mbo.getString("worktype"), 11L);
			work.setValue("description", mbo.getString("description"), 11L);
			work.setValue("udlevel", mbo.getString("worklevel"), 11L);
			work.setValue("status", "WAPPR", 11L);
			work.setValue("targstartdate", mbo.getDate("planstartdate"), 11L);
			work.setValue("targcompdate", mbo.getDate("planenddate"), 11L);
			work.setValue("lead", mbo.getString("workleader"), 11L);
			work.setValue("udworkinggroup", mbo.getString("workgroup"), 11L);
		}
		workSet.save();
	}
}
