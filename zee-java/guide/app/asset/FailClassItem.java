package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class FailClassItem extends Mbo implements MboRemote {

	public FailClassItem(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof FailClass) {
			String failclassnum = parent.getString("FAILCLASSNUM");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("failclassnum", failclassnum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
