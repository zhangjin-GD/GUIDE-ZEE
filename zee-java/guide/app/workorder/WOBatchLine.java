package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class WOBatchLine extends Mbo implements MboRemote {

	public WOBatchLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String wobatchnum = parent.getString("wobatchnum");
			this.setValue("linenum", linenum, 11L);
			this.setValue("wobatchnum", wobatchnum, 11L);
		}
	}
}
