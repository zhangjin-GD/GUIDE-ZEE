package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldAssetFaildur extends MboValueAdapter {

	public FldAssetFaildur(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double failDur = 0.00d;
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote woSet = mbo.getMboSet("UDFAILDUR");
		if (!woSet.isEmpty() && woSet.count() > 0) {
			failDur = woSet.sum("udfaildur");
		}
		this.getMboValue().setValue(failDur, 11L);
	}
	
}
