package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMbo;
import psdi.mbo.NonPersistentMboRemote;

public class VContRFQ  extends NonPersistentMbo implements NonPersistentMboRemote{

	public VContRFQ(MboSet ms) throws RemoteException {
		super(ms);
	}

}
