package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatDspoToLineCost extends MboValueAdapter {

	public FldMatDspoToLineCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double orderqty1 = mbo.getDouble("orderqty1");
		double unitcost1 = mbo.getDouble("unitcost1");
		double orderqty2 = mbo.getDouble("orderqty2");
		double unitcost2 = mbo.getDouble("unitcost2");
		double linecost1 = orderqty1 * unitcost1;
		double linecost2 = orderqty2 * unitcost2;
		double linecost = linecost1 + linecost2;
		mbo.setValue("linecost1", linecost1, 11L);
		mbo.setValue("linecost2", linecost2, 11L);
		mbo.setValue("linecost", linecost, 11L);
	}
}
