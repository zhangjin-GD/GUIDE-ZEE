package guide.app.company;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class CompGradeLine extends Mbo implements MboRemote {

	public CompGradeLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String cgnum = parent.getString("cgnum");
			this.setValue("linenum", linenum, 11L);
			this.setValue("cgnum", cgnum, 11L);
		}
	}
}
