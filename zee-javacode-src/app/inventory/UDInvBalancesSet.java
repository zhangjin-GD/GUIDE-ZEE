package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.InvBalancesSet;
import psdi.app.inventory.InvBalancesSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDInvBalancesSet extends InvBalancesSet implements InvBalancesSetRemote {

	public UDInvBalancesSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDInvBalances(ms);
	}
}
