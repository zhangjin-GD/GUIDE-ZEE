package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldInvStockLineQuantity extends MboValueAdapter {

	public FldInvStockLineQuantity(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		Mbo mbo = this.getMboValue().getMbo();
		double quantity = this.getMboValue().getDouble();
		double curbal = mbo.getDouble("CURBAL");

		if (!this.getMboValue().isNull()) {
			double differqty = quantity - curbal;
			mbo.setValue("differqty", differqty, 11L);
		} else {
			mbo.setValueNull("differqty", 11L);
		}

	}
}
