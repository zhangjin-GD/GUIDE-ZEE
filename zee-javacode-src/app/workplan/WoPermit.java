package guide.app.workplan;

import java.rmi.RemoteException;

import guide.app.workorder.UDWO;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class WoPermit extends Mbo implements MboRemote {

	public WoPermit(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof WorkPlan) {
			String plannum = parent.getString("plannum");
			this.setValue("plannum", plannum, 11L);
		} else if (parent != null && parent instanceof UDWO) {
			String wonum = parent.getString("wonum");
			this.setValue("wonum", wonum, 11L);
		}
	}

}
