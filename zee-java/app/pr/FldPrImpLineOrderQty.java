package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPrImpLineOrderQty extends MboValueAdapter {

	public FldPrImpLineOrderQty(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		double orderqty = mbo.getDouble("orderqty");
		double unitcost = mbo.getDouble("unitcost");
		mbo.setValue("linecost", orderqty * unitcost, 2L);
	}
}
