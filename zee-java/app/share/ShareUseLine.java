package guide.app.share;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;

public class ShareUseLine extends Mbo implements MboRemote{

	public ShareUseLine(MboSet ms) throws RemoteException {
		super(ms);
	}

}
