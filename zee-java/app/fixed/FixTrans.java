package guide.app.fixed;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;

public class FixTrans extends UDMbo implements MboRemote{

	public FixTrans(MboSet ms) throws RemoteException {
		super(ms);
	}

}
