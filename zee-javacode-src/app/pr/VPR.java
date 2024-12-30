package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMbo;
import psdi.mbo.custapp.NonPersistentCustomMboRemote;

public class VPR extends NonPersistentCustomMbo implements NonPersistentCustomMboRemote {

	public VPR(MboSet ms) throws RemoteException {
		super(ms);
	}

}
