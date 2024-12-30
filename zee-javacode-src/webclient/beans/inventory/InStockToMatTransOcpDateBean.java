package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.inventory.MatTransOcp;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class InStockToMatTransOcpDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.getParent().getMbo();
		if (owner != null) {
			String location = owner.getString("location");
			for (int i = 0; i < vector.size(); i++) {

				MboRemote mr = (MboRemote) vector.elementAt(i);
				String itemnum = mr.getString("itemnum");
				double vorderqty = mr.getDouble("vorderqty");
				MboSetRemote inventorySet = owner.getMboSet("$UDINVENTORY" + i, "UDINVENTORY",
						"location='" + location + "' and itemnum='" + itemnum + "'");
				if (vorderqty > 0) {
					if (!inventorySet.isEmpty() && inventorySet.count() > 0) {
						MboRemote inventory = inventorySet.getMbo(0);
						createMatTranSocp(inventory, mr);
					} else {
						MboRemote inventory = inventorySet.add();
						inventory.setValue("itemnum", itemnum, 11L);
						inventory.setValue("location", location, 11L);
						createMatTranSocp(inventory, mr);
					}
				}
			}
		}
		return super.execute();
	}

	private void createMatTranSocp(MboRemote inventory, MboRemote mr) throws RemoteException, MXException {
		MboSetRemote matTransSet = inventory.getMboSet("UDMATTRANSOCP");
		MatTransOcp matTrans = (MatTransOcp) matTransSet.add();
		matTrans.setValue("issuetype", "RECEIPT", 11L);
		matTrans.setValue("itemnum", mr.getString("itemnum"), 2L);
		matTrans.setValue("orderqty", mr.getDouble("vorderqty"), 2L);
		matTrans.setValue("totalprice", mr.getDouble("vtotalprice"), 2L);
	}
}
