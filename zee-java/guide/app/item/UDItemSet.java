package guide.app.item;

import java.rmi.RemoteException;

import psdi.app.item.ItemSet;
import psdi.app.item.ItemSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDItemSet extends ItemSet implements ItemSetRemote{

	public UDItemSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDItem(ms);
	}
}
