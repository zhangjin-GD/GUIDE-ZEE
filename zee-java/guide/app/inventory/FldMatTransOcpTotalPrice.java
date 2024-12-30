package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatTransOcpTotalPrice extends MboValueAdapter {

	public FldMatTransOcpTotalPrice(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();

		double orderqty = mbo.getDouble("ORDERQTY");
		double totalprice = mbo.getDouble("TOTALPRICE");

		double totalcost = orderqty * totalprice;
		mbo.setValue("totalcost", totalcost, 2L);
	}
}
