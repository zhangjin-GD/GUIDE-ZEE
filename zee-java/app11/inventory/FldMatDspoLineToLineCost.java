package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatDspoLineToLineCost extends MboValueAdapter {

	public FldMatDspoLineToLineCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double orderqty = mbo.getDouble("ORDERQTY");
		double unitcost = mbo.getDouble("UNITCOST");
		double linecost = orderqty * unitcost;
		mbo.setValue("linecost", linecost, 11L);
	}
}
