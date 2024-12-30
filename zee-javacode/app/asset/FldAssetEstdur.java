package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldAssetEstdur extends MboValueAdapter {

	public FldAssetEstdur(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double estDur = 0.00d;
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote woSet = mbo.getMboSet("UDESTDUR");
		if (!woSet.isEmpty() && woSet.count() > 0) {
			estDur = woSet.sum("estdur");
		}
		this.getMboValue().setValue(estDur, 11L);
	}
	
}
