package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.InventorySet;
import psdi.app.inventory.InventorySetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDInventorySet extends InventorySet implements InventorySetRemote {

	public UDInventorySet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDInventory(ms);
	}
}
