package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldProconPretaxCost extends MboValueAdapter {

	public FldProconPretaxCost(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double totalcost = mbo.getDouble("totalcost");
		double tax = mbo.getDouble("tax");
		double pretaxcost = totalcost - tax;
		mbo.setValue("pretaxcost", pretaxcost, 11L);
	}
}
