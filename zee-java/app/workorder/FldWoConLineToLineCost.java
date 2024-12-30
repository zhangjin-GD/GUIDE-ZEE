package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWoConLineToLineCost extends MboValueAdapter {

	public FldWoConLineToLineCost(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		double orderqty = mbo.getDouble("orderqty");
		double unitcost = mbo.getDouble("unitcost");
		double linecost = orderqty * unitcost;
		mbo.setValue("linecost", linecost, 2L);
	}
}
