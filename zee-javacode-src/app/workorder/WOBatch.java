package guide.app.workorder;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;

public class WOBatch extends UDMbo implements MboRemote{

	public WOBatch(MboSet ms) throws RemoteException {
		super(ms);
	}

}
