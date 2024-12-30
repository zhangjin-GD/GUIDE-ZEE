package guide.app.pr;

import java.rmi.RemoteException;

import psdi.app.pr.PRSet;
import psdi.app.pr.PRSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDPRSet extends PRSet implements PRSetRemote {

	public UDPRSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet var1) throws MXException, RemoteException {
		return new UDPR(var1);
	}

}
