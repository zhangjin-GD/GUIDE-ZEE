package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMbo;
import psdi.mbo.custapp.NonPersistentCustomMboRemote;

public class VAssetTstrans extends NonPersistentCustomMbo implements NonPersistentCustomMboRemote{

	public VAssetTstrans(MboSet ms) throws RemoteException {
		super(ms);
	}

}
