package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSet;
import psdi.util.MXException;

public class VmatsafeLineSet extends NonPersistentCustomMboSet {

	public VmatsafeLineSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet var1) throws MXException, RemoteException {
		return new VmatsafeLine(var1);
	}

	@Override
	public MboRemote setup() throws MXException, RemoteException {
		return this.getMbo(0);
	}

}
