package guide.app.fixed;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class FixedAssetClCp extends Mbo implements MboRemote {

	public FixedAssetClCp(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			String faclassnum = parent.getString("faclassnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("faclassnum", faclassnum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
