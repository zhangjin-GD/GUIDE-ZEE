package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWOSettleLineEstCost extends MboValueAdapter{

	public FldWOSettleLineEstCost(MboValue mbv) {
		super(mbv);
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		if (parent != null && parent instanceof WOSettle) {
			parent.getMboSet("UDWOSETTLELINE").resetQbe();
			double estcost = mbo.getThisMboSet().sum("estcost");
			parent.setValue("totalcost", estcost, 11L);
		}
	}

}
