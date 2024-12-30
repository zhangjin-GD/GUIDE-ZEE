package guide.app.common;

import java.rmi.RemoteException;

import psdi.app.person.FldPersonID;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldComPersonId extends FldPersonID {

	public FldComPersonId(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		if (!this.getMboValue().isNull()) {
			String value = this.getMboValue().getString();
			MboSetRemote personSet = mbo.getMboSet("$PERSON", "PERSON", "personid ='" + value + "'");
			if (personSet != null && !personSet.isEmpty()) {
				MboRemote person = personSet.getMbo(0);
				mbo.setValue("udcompany", person.getString("udcompany"), 11L);
				mbo.setValue("uddept", person.getString("uddept"), 11L);
				mbo.setValue("udofs", person.getString("udofs"), 11L);
			}
		} else {
			mbo.setValueNull("udcompany");
			mbo.setValueNull("uddept");
			mbo.setValueNull("udofs");
		}
	}
}
