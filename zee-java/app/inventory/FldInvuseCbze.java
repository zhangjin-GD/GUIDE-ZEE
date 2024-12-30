package guide.app.inventory;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

import java.rmi.RemoteException;

public class FldInvuseCbze extends MboValueAdapter {

	public FldInvuseCbze(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote mbo = getMboValue().getMbo();
		MboSetRemote mboSet = mbo.getMboSet("INVUSELINE");
		if (!mboSet.isEmpty() && mboSet.count() > 0) {
			double linecost = mboSet.sum("LINECOST");
			mbo.setValue("udcbze", linecost, 11L);
		}
	}
}
