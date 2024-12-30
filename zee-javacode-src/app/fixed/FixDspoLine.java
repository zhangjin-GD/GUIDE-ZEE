package guide.app.fixed;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class FixDspoLine extends Mbo implements MboRemote {

	public FixDspoLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof FixDspo) {
			String fixdsponum = parent.getString("fixdsponum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("fixdsponum", fixdsponum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
