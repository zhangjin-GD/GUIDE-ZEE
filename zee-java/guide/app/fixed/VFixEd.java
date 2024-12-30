package guide.app.fixed;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMbo;
import psdi.mbo.NonPersistentMboRemote;

public class VFixEd extends NonPersistentMbo implements NonPersistentMboRemote {

	public VFixEd(MboSet ms) throws RemoteException {
		super(ms);
	}

}
