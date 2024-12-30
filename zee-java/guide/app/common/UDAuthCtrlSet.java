package guide.app.common;


import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;


public class UDAuthCtrlSet extends MboSet implements MboSetRemote {

	public UDAuthCtrlSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	 protected Mbo getMboInstance(MboSet mboset) throws MXException, RemoteException
	  {
	    return new UDAuthCtrl(mboset);
	  }

}
