package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPrImpLineLineCost extends MboValueAdapter {

	public FldPrImpLineLineCost(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		if (parent != null && parent instanceof PRImp) {
			parent.getMboSet("UDPRIMPLINE").resetQbe();
			double linecost = mbo.getThisMboSet().sum("linecost");
			parent.setValue("totalcost", linecost, 11L);
		}
	}
}
