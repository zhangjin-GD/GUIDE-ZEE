package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldAssetMatCost extends MboValueAdapter {

	public FldAssetMatCost(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double linecost = 0.00d;
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote matusetransSet = mbo.getMboSet("MATUSETRANS");
		if (!matusetransSet.isEmpty() && matusetransSet.count() > 0) {
			linecost = matusetransSet.sum("linecost");
		}
		this.getMboValue().setValue(linecost, 11L);
	}
	
}
