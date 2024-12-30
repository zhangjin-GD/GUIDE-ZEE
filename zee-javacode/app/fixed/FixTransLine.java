package guide.app.fixed;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class FixTransLine extends Mbo implements MboRemote {

	public FixTransLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote owner = this.getOwner();
		if (owner != null) {
			if (owner instanceof FixTrans) {
				String fixtransnum = owner.getString("fixtransnum");
				this.setValue("fixtransnum", fixtransnum, 11L);
			}
			int linenum = (int) (this.getThisMboSet().max("linenum") + 1);
			this.setValue("linenum", linenum, 11L);
		}
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
	}
}
