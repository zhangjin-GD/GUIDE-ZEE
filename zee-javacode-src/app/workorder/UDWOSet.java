package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.app.workorder.WOSet;
import psdi.app.workorder.WOSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDWOSet extends WOSet implements WOSetRemote {
	
	MboRemote mbo = null;

	public UDWOSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDWO(ms);
	}
	
}
