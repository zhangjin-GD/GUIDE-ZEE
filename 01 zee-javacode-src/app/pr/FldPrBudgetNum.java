package guide.app.pr;

import java.rmi.RemoteException;

import guide.app.common.FldBudgetNum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPrBudgetNum extends FldBudgetNum {

	public FldPrBudgetNum(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote prLineSet = mbo.getMboSet("PRLINE");
		if (prLineSet != null && !prLineSet.isEmpty()) {
			String udbudgetnum = mbo.getString("udbudgetnum");
			for (int i = 0; prLineSet.getMbo(i) != null; i++) {
				MboRemote prLine = prLineSet.getMbo(i);
				prLine.setValue("udbudgetnum", udbudgetnum, 11L);
			}
		}
	}

}
