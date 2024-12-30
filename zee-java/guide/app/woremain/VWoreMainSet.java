package guide.app.woremain;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSetRemote;
import psdi.util.MXException;

public class VWoreMainSet extends NonPersistentCustomMboSet implements NonPersistentCustomMboSetRemote {

	public VWoreMainSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {

		return new VWoreMain(ms);
	}

	@Override
	public MboRemote setup() throws MXException, RemoteException {
		MboRemote owner = this.getOwner();
		MboRemote addMbo = this.addAtEnd();
		if (owner != null) {
			String assetnum = "";
			String udcompany = owner.getString("udcompany");
			if (owner instanceof WoreMain) {
				String woremainnum = owner.getString("woremainnum");
				addMbo.setValue("woremainnum", woremainnum, 11L);
			}
			if (owner instanceof WoreTask) {
				assetnum = owner.getString("assetnum");
			}
			addMbo.setValue("udcompany", udcompany, 11L);
			addMbo.setValue("assetnum", assetnum, 11L);
		}
		return addMbo;
	}
}
