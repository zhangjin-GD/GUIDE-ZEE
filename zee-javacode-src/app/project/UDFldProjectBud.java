package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldProjectBud extends MboValueAdapter{

	public UDFldProjectBud(MboValue mbv) {
		super(mbv);
	}
	
	public void initValue() throws MXException, RemoteException {
		super.initValue();
	    /**
		 * ZEE-显示项目占用预算、项目剩余预算
		 * 2025-02-21 14:35:47
		 */
		double udwoprojectbud = 0.00d;
		double udprojectcost = 0.00d;
		MboRemote mbo = this.getMboValue().getMbo();//udproject
		udprojectcost = mbo.getDouble("budgetcost");
		MboSetRemote woSet = mbo.getMboSet("UDWORKORDER");
		if (woSet != null && !woSet.isEmpty()) {
			for(int i = 0; i<woSet.count(); i++){
				MboSetRemote wouseSet = woSet.getMbo(i).getMboSet("MATUSETRANS");
				if (wouseSet != null && !wouseSet.isEmpty()) {
					udwoprojectbud = udwoprojectbud + wouseSet.sum("linecost");
				}
			}
		}
		MboSetRemote matrecSet = mbo.getMboSet("UDMATRECTRANS");
		if (matrecSet != null && !matrecSet.isEmpty()) {
			udwoprojectbud = udwoprojectbud + matrecSet.sum("linecost");
		}
		mbo.setValue("udprojectbudocu", udwoprojectbud, 11L); //wo:实时更新project-code占用
		mbo.setValue("udprojectbud", udprojectcost - udwoprojectbud, 11L); //wo:实时更新project-code剩余预算
	}
	
}
