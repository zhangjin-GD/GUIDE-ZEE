package guide.app.fixed;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class FixStockLine extends Mbo implements MboRemote {

	public FixStockLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof FixStock) {
			String fixstocknum = parent.getString("fixstocknum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("fixstocknum", fixstocknum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
