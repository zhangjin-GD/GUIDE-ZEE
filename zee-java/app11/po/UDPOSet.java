package guide.app.po;

import java.rmi.RemoteException;

import psdi.app.po.POSet;
import psdi.app.po.POSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDPOSet extends POSet implements POSetRemote {

	public UDPOSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet var1) throws MXException, RemoteException {
		return new UDPO(var1);
	}
}
