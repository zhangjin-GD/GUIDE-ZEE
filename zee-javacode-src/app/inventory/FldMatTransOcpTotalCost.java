package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatTransOcpTotalCost extends MboValueAdapter {

	public FldMatTransOcpTotalCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();

		if (parent != null) {
			parent.getMboSet("UDMATTRANSOCP").resetQbe();
			double totalcost = mbo.getThisMboSet().sum("totalcost");
			parent.setValue("totalcost", totalcost, 2L);
		}
	}
}
