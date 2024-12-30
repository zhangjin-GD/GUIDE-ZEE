package guide.app.common;

import java.rmi.RemoteException;

import psdi.common.commlog.CommLogSet;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;


public class UDCommLogSet extends CommLogSet {

	public UDCommLogSet(MboServerInterface ms) throws MXException,
			RemoteException {
		super(ms);
	}
	
	  protected Mbo getMboInstance(MboSet mboset) throws MXException, RemoteException
	  {
	    return new UDCommLog(mboset);
	  }
	  
}
