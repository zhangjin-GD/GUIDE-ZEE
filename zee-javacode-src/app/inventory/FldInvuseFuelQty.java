package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldInvuseFuelQty extends MboValueAdapter {

	public FldInvuseFuelQty(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double fuelQty = 0.00d;
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote fuelQtySet = mbo.getMboSet("UDFUELQTY");
		if (!fuelQtySet.isEmpty() && fuelQtySet.count() > 0) {
			fuelQty = fuelQtySet.sum("quantity");
		}
		mbo.setValue("udfuelqty", fuelQty, 11L);
	}
}
