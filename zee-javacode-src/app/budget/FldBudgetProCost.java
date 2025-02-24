package guide.app.budget;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldBudgetProCost extends MboValueAdapter {

	public FldBudgetProCost(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double linecost = 0.00d;
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote costSet = mbo.getMboSet("procost");
		if (costSet != null && !costSet.isEmpty()) {
			linecost = costSet.sum("linecost");
		}
		/** 
		 * ZEE - 预算管理应用程序关联显示项目project-code金额
		 * 2025-2-12  10:17  
		 * 27-37
		 */
		if(mbo.getString("udcompany").equalsIgnoreCase("ZEE")){
			costSet = mbo.getMboSet("udprocost");
			if (costSet != null && !costSet.isEmpty()) {
				linecost = costSet.sum("budgetcost");
			}
		}
		this.getMboValue().setValue(linecost, 11L);
	}
}