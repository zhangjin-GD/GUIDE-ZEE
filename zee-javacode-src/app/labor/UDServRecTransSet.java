package guide.app.labor;


import java.rmi.RemoteException;

import psdi.app.labor.ServRecTransSet;
import psdi.app.labor.ServRecTransSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDServRecTransSet extends ServRecTransSet implements ServRecTransSetRemote{

	public UDServRecTransSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}
	
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDServRecTrans(ms);
	}

}
