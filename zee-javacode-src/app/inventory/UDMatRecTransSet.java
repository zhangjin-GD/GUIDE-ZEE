package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.MatRecTransSet;
import psdi.app.inventory.MatRecTransSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDMatRecTransSet extends MatRecTransSet implements MatRecTransSetRemote{

	public UDMatRecTransSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}
	
	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDMatRecTrans(ms);
	}
	
}
