package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSetRemote;
import psdi.util.MXException;

public class VPOSet extends NonPersistentCustomMboSet implements NonPersistentCustomMboSetRemote {

	public VPOSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {

		return new VPO(ms);
	}

	@Override
	public MboRemote setup() throws MXException, RemoteException {
		MboRemote owner = this.getOwner();
		MboRemote mbo = null;
		if (owner != null && owner instanceof UDPO) {
			String ponum = owner.getString("ponum");
			int revisionnum = owner.getInt("revisionnum");
			mbo = this.addAtEnd();
			mbo.setValue("ponum", ponum, 11L);
			mbo.setValue("revisionnum", revisionnum, 11L);
		}
		return mbo;
	}
}
