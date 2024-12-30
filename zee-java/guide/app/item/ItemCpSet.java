package guide.app.item;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class ItemCpSet extends MboSet implements MboSetRemote {

	public ItemCpSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet arg0) throws MXException, RemoteException {
		return new ItemCp(arg0);
	}

}
