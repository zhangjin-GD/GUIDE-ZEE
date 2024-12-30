package guide.app.inventory;

import java.rmi.RemoteException;

import guide.app.common.FldBudgetNum;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldInvUseBudgetNum extends FldBudgetNum {

	public FldInvUseBudgetNum(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		String udbudgetnum = this.getMboValue().getString();
		MboSetRemote lineSet = mbo.getMboSet("INVUSELINE");
		if (lineSet != null && !lineSet.isEmpty()) {
			for (int i = 0; lineSet.getMbo(i) != null; i++) {
				MboRemote line = lineSet.getMbo(i);
				line.setValue("udbudgetnum", udbudgetnum, 11L);
			}
		}
	}
}
