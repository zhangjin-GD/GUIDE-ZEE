package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class PRVendor extends Mbo implements MboRemote {

	public PRVendor(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof UDPR) {
			String prnum = parent.getString("prnum");
			this.setValue("prnum", prnum, 11L);
		}
	}
}
