package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class WOSettleLine extends Mbo implements MboRemote {

	public WOSettleLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof WOSettle) {
			String wosettlenum = parent.getString("wosettlenum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;

			this.setValue("wosettlenum", wosettlenum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
