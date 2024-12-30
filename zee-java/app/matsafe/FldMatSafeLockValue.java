package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatSafeLockValue extends MboValueAdapter {

	public FldMatSafeLockValue(MboValue mbovalue) {
		super(mbovalue);
	}

	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double lockvalue2 = 0;
		double lockvalue1 = mbo.getDouble("lockvalue");
		String assetnum = mbo.getString("assetnum");
		String matsafetype = mbo.getString("matsafetype");
		String part = mbo.getString("part");
		String matsafedesc = mbo.getString("matsafedesc");
		MboSetRemote matsafeSet = mbo.getMboSet("$UDMATSAFE", "UDMATSAFE",
				"assetnum='" + assetnum + "' and matsafetype='" + matsafetype + "' and part='" + part
						+ "' and matsafedesc='" + matsafedesc + "' and upperdate < :upperdate");
		matsafeSet.setOrderBy("upperdate desc");
		matsafeSet.reset();

		if (!matsafeSet.isEmpty() && matsafeSet.count() > 0) {
			MboRemote matsafe = matsafeSet.getMbo(0);
			lockvalue2 = matsafe.getDouble("lockvalue");
		}

		if (lockvalue1 <= 0) {
			mbo.setValue("lockdiff", 0, 11L);
		} else {
			double lockdiff = lockvalue1 - lockvalue2;
			mbo.setValue("lockdiff", lockdiff, 11L);
		}

	}
}
