package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class ShorePowerLine extends Mbo implements MboRemote {

	public ShorePowerLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof ShorePower) {
			String spnum = parent.getString("spnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("spnum", spnum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
