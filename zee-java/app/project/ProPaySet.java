package guide.app.project;

import java.rmi.RemoteException;

import guide.app.common.UDMboSet;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class ProPaySet extends UDMboSet implements MboSetRemote {

	public ProPaySet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new ProPay(ms);
	}

}
