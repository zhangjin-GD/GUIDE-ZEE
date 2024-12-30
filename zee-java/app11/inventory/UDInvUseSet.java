package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.InvUseSet;
import psdi.app.inventory.InvUseSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDInvUseSet extends InvUseSet implements InvUseSetRemote{

	public UDInvUseSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}
	
	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDInvUse(ms);
	}
}
