package guide.app.company;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;

public class CompRecord extends UDMbo implements MboRemote {

	public CompRecord(MboSet ms) throws RemoteException {
		super(ms);
	}

}
