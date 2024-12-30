package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPrLineCurBalTotal extends MboValueAdapter {

	public FldPrLineCurBalTotal(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double curbal = 0.00d;
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote mboSet = mbo.getMboSet("UDINVBALCURBAL");
		if (mboSet != null && !mboSet.isEmpty()) {
			curbal = mboSet.sum("curbal");
		}
		this.getMboValue().setValue(curbal, 11L);
	}
}
