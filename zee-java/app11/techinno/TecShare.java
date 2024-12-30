package guide.app.techinno;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;

public class TecShare extends UDMbo implements MboRemote{

	public TecShare(MboSet ms) throws RemoteException {
		super(ms);
	}

}
