package guide.app.po.virtual;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMboSet;
import psdi.util.MXException;

public class UDPORevisionInputSet extends NonPersistentMboSet {
	
	public UDPORevisionInputSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDPORevisionInput(ms);
	}

}