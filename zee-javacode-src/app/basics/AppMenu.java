package guide.app.basics;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class AppMenu extends Mbo implements MboRemote {

	public AppMenu(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof AppMenu) {
			this.setValue("parent", parent.getString("appmenunum"), 11L);
		}
	}
}
