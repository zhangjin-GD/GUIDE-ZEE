package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatTransOcpOrderQty extends MboValueAdapter {

	public FldMatTransOcpOrderQty(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();

		double orderqty = mbo.getDouble("ORDERQTY");
		double totalprice = mbo.getDouble("TOTALPRICE");

		double totalcost = orderqty * totalprice;
		mbo.setValue("TOTALCOST", totalcost, 2L);
		if (parent != null) {
			parent.getMboSet("UDMATTRANSOCP").resetQbe();
			double totalqty = mbo.getThisMboSet().sum("orderqty");
			parent.setValue("totalqty", totalqty, 2L);
		}
	}
}
