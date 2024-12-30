package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMeasurePointValue extends MboValueAdapter {

	public FldMeasurePointValue(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		double valueinit = mbo.getDouble("valueinit");
		double value = mbo.getDouble("value");
		double valuelast = valueinit + value;
		mbo.setValue("valuelast", valuelast, 11L);
	}
}
