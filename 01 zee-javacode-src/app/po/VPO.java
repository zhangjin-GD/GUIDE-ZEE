package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMbo;
import psdi.mbo.custapp.NonPersistentCustomMboRemote;

public class VPO extends NonPersistentCustomMbo implements NonPersistentCustomMboRemote{

	public VPO(MboSet ms) throws RemoteException {
		super(ms);
	}

}
