package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldAvlBal extends MboValueAdapter {

	public FldAvlBal(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double qty = 0, invuseQty = 0, borrowQty = 0, returnQty = 0;
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote lineSet = mbo.getMboSet("UDINVUSELINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			invuseQty = lineSet.sum("quantity");
		}
		MboSetRemote borrowSet = mbo.getMboSet("UDSHAREUSELINE_BORROW");
		if (!borrowSet.isEmpty() && borrowSet.count() > 0) {
			borrowQty = borrowSet.sum("orderqty");
		}
		MboSetRemote returnSet = mbo.getMboSet("UDSHAREUSELINE_RETURN");
		if (!returnSet.isEmpty() && returnSet.count() > 0) {
			returnQty = returnSet.sum("orderqty");
		}
		qty = invuseQty + borrowQty - returnQty;
		this.getMboValue().setValue(qty, 11L);
	}
}
