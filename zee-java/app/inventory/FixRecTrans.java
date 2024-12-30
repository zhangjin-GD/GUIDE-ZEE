package guide.app.inventory;

import java.rmi.RemoteException;

import guide.app.po.UDPO;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class FixRecTrans extends Mbo implements MboRemote {

	public FixRecTrans(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof UDPO) {
			String ponum = parent.getString("ponum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("ponum", ponum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
//		if (this.toBeAdded() || this.toBeDeleted()) {
//			MboSetRemote polineSet = this.getMboSet("poline");
//			if (!polineSet.isEmpty() && polineSet.count() > 0) {
//				MboRemote poline = polineSet.getMbo(0);
//				double orderqty = poline.getDouble("orderqty");
//				MboSetRemote poFixSet = this.getMboSet("PONUMFIXRECTRANS");
//				double quantity = poFixSet.sum("quantity");
//				double receivedqty = orderqty - quantity;
//				poline.setValue("receivedqty", receivedqty, 11L);
//				if (receivedqty <= 0) {
//					poline.setValue("receiptscomplete", true, 2L);
//				}
//			}
//		}
	}
}
