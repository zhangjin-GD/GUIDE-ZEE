package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMbo;
import psdi.mbo.custapp.NonPersistentCustomMboRemote;

public class VPRLine extends NonPersistentCustomMbo implements NonPersistentCustomMboRemote{

	public VPRLine(MboSet ms) throws RemoteException {
		super(ms);
	}

}
