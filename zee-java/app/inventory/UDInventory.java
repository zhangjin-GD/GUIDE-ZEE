package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.Inventory;
import psdi.app.inventory.InventoryRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDInventory extends Inventory implements InventoryRemote {

	public UDInventory(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	public double calculateCurrentBalanceCom() throws MXException, RemoteException {
		return this.getMboSet("UDINVBALCOM").sum("curbal");
	}
}
