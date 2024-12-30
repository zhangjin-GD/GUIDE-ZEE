package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldInvBalancesLotSort extends MboValueAdapter {

	public FldInvBalancesLotSort(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote mbo = this.getMboValue().getMbo();
		int invbalancesid1 = mbo.getInt("invbalancesid");
		MboSetRemote invbalancesSet = mbo.getMboSet("$invbalances", "invbalances",
				"curbal>0 and itemnum=:itemnum and location=:location");
		invbalancesSet.setOrderBy("itemnum,physcntdate,invbalancesid");
		invbalancesSet.reset();
		if (!invbalancesSet.isEmpty() && invbalancesSet.count() > 0) {
			for (int i = 0; invbalancesSet.getMbo(i) != null; i++) {
				MboRemote invbalances = invbalancesSet.getMbo(i);
				int invbalancesid2 = invbalances.getInt("invbalancesid");
				if (invbalancesid1 == invbalancesid2) {
					int lotsort = i + 1;
					this.getMboValue().setValue(lotsort, 11L);
				}
			}
		}
	}

}
