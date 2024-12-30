package guide.app.po.virtual;

import java.rmi.RemoteException;

import guide.app.po.UDPO;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMbo;
import psdi.util.MXException;

public class UDPORevisionInput extends NonPersistentMbo {

	public UDPORevisionInput(MboSet ms) throws RemoteException {
		super(ms);
	}

	public void add() throws MXException, RemoteException {

		super.add();
		MboRemote owner = this.getOwner();
		if (owner != null && owner instanceof UDPO) {
			this.setValue("udrevponum", owner.getString("udrevponum"), 11L);
			this.setValue("udrevnum", owner.getInt("udrevnum") + 1.0D, 11L);
			this.setValue("revdescription", owner.getString("description"), 11L);
		}
	}
}
