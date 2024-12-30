package guide.app.matsafe;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.custapp.NonPersistentCustomMboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class VmatsafeSet extends NonPersistentCustomMboSet {

	public VmatsafeSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet var1) throws MXException, RemoteException {
		return new Vmatsafe(var1);
	}

	@Override
	public MboRemote setup() throws MXException, RemoteException {
		MboRemote mbo = this.add();
		String personId = this.getUserInfo().getPersonId();
		Date sysDate = MXServer.getMXServer().getDate();
		MboSetRemote personSet = mbo.getMboSet("$PERSON", "PERSON", "personid ='" + personId + "'");
		if (personSet != null && !personSet.isEmpty()) {
			MboRemote person = personSet.getMbo(0);
			mbo.setValue("udcompany", person.getString("udcompany"), 11L);
		}
		mbo.setValue("upperdate", sysDate, 11L);
		return mbo;
	}
}
