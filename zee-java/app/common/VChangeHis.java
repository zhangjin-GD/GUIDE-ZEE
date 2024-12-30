package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMbo;
import psdi.mbo.custapp.NonPersistentCustomMboRemote;

public class VChangeHis extends NonPersistentCustomMbo implements NonPersistentCustomMboRemote{

	public VChangeHis(MboSet ms) throws RemoteException {
		super(ms);
	}

}
