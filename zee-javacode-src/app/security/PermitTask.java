package guide.app.security;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class PermitTask extends Mbo implements MboRemote {

	public PermitTask(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof Permit) {
			String permitnum = parent.getString("permitnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("permitnum", permitnum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
