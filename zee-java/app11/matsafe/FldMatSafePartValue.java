package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatSafePartValue extends MboValueAdapter {

	public FldMatSafePartValue(MboValue mbovalue) {
		super(mbovalue);
	}

	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double partvalue2 = 0;
		double partvalue1 = mbo.getDouble("partvalue");
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
			partvalue2 = matsafe.getDouble("partvalue");
		}

		if (partvalue1 <= 0) {
			mbo.setValue("partdiff", 0, 11L);
		} else {
			double partdiff = partvalue1 - partvalue2;
			mbo.setValue("partdiff", partdiff, 11L);
		}

	}
}
