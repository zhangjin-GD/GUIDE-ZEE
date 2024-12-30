package guide.app.techinno;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;

public class ProReview  extends UDMbo implements MboRemote {

	public ProReview(MboSet ms) throws RemoteException {
		super(ms);
	}

}
