package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWoConLineLineCost extends MboValueAdapter {

	public FldWoConLineLineCost(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		if (parent != null && parent instanceof UDWO) {
			parent.getMboSet("UDWOCONTRACTLINE").resetQbe();
			double linecost = mbo.getThisMboSet().sum("linecost");
			parent.setValue("udestcost", linecost, 11L);
		}
	}
}
