package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDGpmSequence extends Mbo implements MboRemote {

	public UDGpmSequence(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
//		try {
//			int linenum = this.getInt("linenum");
//			String[] attrs1 = { "linenum" };
//			if (linenum == 1) {
//				this.setFieldFlag(attrs1, 7L, true);
//			} else {
//				this.setFieldFlag(attrs1, 7L, false);
//			}
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		} catch (MXException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof UDGpm) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("linenum", linenum, 11L);
			this.setValue("gpmnum", parent.getString("gpmnum"), 11L);
		}
	}
}
