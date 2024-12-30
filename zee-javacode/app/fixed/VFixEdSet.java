package guide.app.fixed;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMboSet;
import psdi.mbo.NonPersistentMboSetRemote;
import psdi.util.MXException;

public class VFixEdSet extends NonPersistentMboSet implements NonPersistentMboSetRemote {

	public VFixEdSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new VFixEd(ms);
	}

	@Override
	public MboRemote setup() throws MXException, RemoteException {
		MboRemote owner = this.getOwner();
		MboRemote mbo = null;
		if (owner != null) {
			MboRemote parent = owner.getOwner();
			if (parent != null) {
				String udcompany = parent.getString("udcompany");
				String uddept = owner.getString("enterby.uddept");
				String enterby = owner.getString("enterby");
				int matrectransid = owner.getInt("matrectransid");
				mbo = this.addAtEnd();
				mbo.setValue("udcompany", udcompany, 11L);
				mbo.setValue("deptmg", uddept, 11L);
				mbo.setValue("administrator", enterby, 11L);
				mbo.setValue("ownerid", matrectransid, 11L);
			}
		}
		return mbo;
	}

}
