package guide.app.pr;

import java.rmi.RemoteException;

import psdi.app.pr.PRLineSet;
import psdi.app.pr.PRLineSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDPRLineSet extends PRLineSet implements PRLineSetRemote {

	public UDPRLineSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet var1) throws MXException, RemoteException {
		return new UDPRLine(var1);
	}
}
