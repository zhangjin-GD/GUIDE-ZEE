package guide.app.security;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class SafePlanLine extends Mbo implements MboRemote {

	public SafePlanLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {

		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof SafePlan) {

			String splnum = parent.getString("splnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;

			this.setValue("splnum", splnum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
