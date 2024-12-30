package guide.app.labor;

import java.rmi.RemoteException;

import psdi.app.labor.Labor;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class LaborQual extends Mbo implements MboRemote {

	public LaborQual(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof Labor) {
			String laborcode = parent.getString("laborcode");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("laborcode", laborcode, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
