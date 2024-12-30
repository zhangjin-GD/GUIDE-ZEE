package guide.app.security;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class PermitTempLine extends Mbo implements MboRemote {

	public PermitTempLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof PermitTemp) {
			String ptnum = parent.getString("ptnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("ptnum", ptnum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
