package guide.app.workplan;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class WorkLabor extends Mbo implements MboRemote {

	public WorkLabor(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			String plannum = parent.getString("plannum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("plannum", plannum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
