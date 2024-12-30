package guide.app.itemreq;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldItemCheck extends MboValueAdapter {

	public FldItemCheck(MboValue mbv) throws MXException {
		super(mbv);
	}

	public void validate() throws RemoteException, MXException {
		MboRemote mbo = getMboValue().getMbo();
		MboSetRemote itemSet = mbo.getMboSet("ITEMCHECK");
		if (!itemSet.isEmpty() && itemSet.count() > 0) {
			throw new MXApplicationException("guide", "1009");
		}
		super.validate();
	}
	
}
