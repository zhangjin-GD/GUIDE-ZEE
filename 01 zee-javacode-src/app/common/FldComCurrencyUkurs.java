package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldComCurrencyUkurs extends FldComCurrencyCode {

	public FldComCurrencyUkurs(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double ukurs = 1;
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote currexchSet = mbo.getMboSet("UDCURREXCH");
		if (!currexchSet.isEmpty() && currexchSet.count() > 0) {
			ukurs = currexchSet.getMbo(0).getDouble("ukurs");
		}
		mbo.setValue("udukurs", ukurs, 11L);
	}
}
