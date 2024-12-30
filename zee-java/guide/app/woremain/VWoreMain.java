package guide.app.woremain;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMbo;
import psdi.mbo.custapp.NonPersistentCustomMboRemote;

public class VWoreMain extends NonPersistentCustomMbo implements NonPersistentCustomMboRemote{

	public VWoreMain(MboSet ms) throws RemoteException {
		super(ms);
	}

}
