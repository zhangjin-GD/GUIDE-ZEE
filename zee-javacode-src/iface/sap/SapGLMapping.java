package guide.iface.sap;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class SapGLMapping extends Mbo implements MboRemote {

	public SapGLMapping(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String personId = this.getUserInfo().getPersonId();
		MboSetRemote personSet = this.getMboSet("$PERSON", "PERSON", "personid ='" + personId + "'");
		if (personSet != null && !personSet.isEmpty()) {
			MboRemote person = personSet.getMbo(0);
			this.setValue("udcompany", person.getString("udcompany"), 11L);
		}
	}
}
