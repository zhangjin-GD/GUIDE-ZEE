package guide.app.fixed;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class FixAccLine extends Mbo implements MboRemote {

	public FixAccLine(MboSet ms) throws RemoteException {
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
		String fixaccnum = null;
		if (owner != null) {
			fixaccnum = owner.getString("fixaccnum");
		}
		int linenum =  (int) (this.getThisMboSet().max("linenum") + 1);
		this.setValue("fixaccnum", fixaccnum);
		this.setValue("linenum", linenum);
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
