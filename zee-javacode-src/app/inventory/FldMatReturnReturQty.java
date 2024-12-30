package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatReturnReturQty extends MboValueAdapter {

	public FldMatReturnReturQty(MboValue mbv) {
		super(mbv);
	}

	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double returqty = mbo.getDouble("returqty");
		double unitcost = mbo.getDouble("invuseline.unitcost");
		double linecost = returqty * unitcost;
		mbo.setValue("linecost", linecost, 11L);
	}
}
