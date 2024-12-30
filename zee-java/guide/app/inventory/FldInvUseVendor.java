package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.company.FldCompany;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldInvUseVendor extends FldCompany {

	public FldInvUseVendor(MboValue mbv) throws MXException {
		super(mbv);
	}

	public void init() throws RemoteException, MXException {
		super.init();

		MboRemote mbo = getMboValue().getMbo();
		setReadonly(mbo);
	}

	private void setReadonly(MboRemote mbo) throws RemoteException, MXException {
		MboSetRemote invuselineSet = mbo.getMboSet("INVUSELINE");
		if (!invuselineSet.isEmpty() && invuselineSet.count() > 0) {
			mbo.setFieldFlag("udvendor", 7L, true);
		} else {
			mbo.setFieldFlag("udvendor", 7L, false);
		}
	}
}
