package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;

public class FailType extends Mbo implements MboRemote {

	public FailType(MboSet ms) throws RemoteException {
		super(ms);
	}

}
