package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMbo;
import psdi.mbo.custapp.NonPersistentCustomMboRemote;

public class VWO extends NonPersistentCustomMbo implements NonPersistentCustomMboRemote{

	public VWO(MboSet ms) throws RemoteException {
		super(ms);
	}

}
