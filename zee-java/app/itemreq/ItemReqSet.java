package guide.app.itemreq;

import java.rmi.RemoteException;

import guide.app.common.UDMboSet;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class ItemReqSet extends UDMboSet implements MboSetRemote {

	public ItemReqSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet var1) throws MXException, RemoteException {
		return new ItemReq(var1);
	}

}
