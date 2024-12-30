package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.location.FldLocation;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldInvmthlyStoreloc extends FldLocation {

	public FldInvmthlyStoreloc(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		if (!this.getMboValue().isNull()) {
			MboSetRemote storelocSet = mbo.getMboSet("STORELOC");
			MboRemote storeloc = storelocSet.getMbo(0);
			String udcompany = storeloc.getString("udcompany");
			String uddept = storeloc.getString("uddept");
			String udofs = storeloc.getString("udofs");
			mbo.setValue("udcompany", udcompany, 11L);
			mbo.setValue("uddept", uddept, 11L);
			mbo.setValue("udofs", udofs, 11L);
		} else {
			mbo.setValueNull("udcompany", 11L);
			mbo.setValueNull("uddept", 11L);
			mbo.setValueNull("udofs", 11L);
		}
	}
}
