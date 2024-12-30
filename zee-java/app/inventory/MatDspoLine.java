package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class MatDspoLine extends Mbo implements MboRemote {

	public MatDspoLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof MatDspo) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String matdsponum = parent.getString("matdsponum");
			this.setValue("linenum", linenum, 11L);
			this.setValue("matdsponum", matdsponum, 11L);
		}
	}
}
