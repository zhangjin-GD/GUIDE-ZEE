package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSetRemote;
import psdi.util.MXException;

public class VChangeHisSet extends NonPersistentCustomMboSet implements NonPersistentCustomMboSetRemote {

	public VChangeHisSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {

		return new VChangeHis(ms);
	}

	@Override
	public MboRemote setup() throws MXException, RemoteException {
		MboRemote owner = this.getOwner();
		MboRemote mbo = null;
		if (owner != null) {
			long valueid = owner.getUniqueIDValue();
			mbo = this.addAtEnd();
			mbo.setValue("ownerid", valueid, 11L);
		}
		return mbo;
	}
}
