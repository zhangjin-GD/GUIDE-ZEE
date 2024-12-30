package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.InvLifoFifoCostSet;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDInvLifoFifoCostSet extends InvLifoFifoCostSet {

	public UDInvLifoFifoCostSet(MboServerInterface ms) throws MXException,
			RemoteException {
		super(ms);
	}
	
	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDInvLifoFifoCost(ms);
	}
}
