package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.InvBalances;
import psdi.app.inventory.InvBalancesRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDInvBalances extends InvBalances implements InvBalancesRemote {

	public UDInvBalances(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void save() throws MXException, RemoteException {
		super.save();

		if (this.toBeAdded()) {
			this.setValue("udnewbinnum", this.getString("binnum"), 11L);
		}
	}
}
