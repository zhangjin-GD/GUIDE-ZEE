package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldGpmMeterToNextValue extends MboValueAdapter {

	public FldGpmMeterToNextValue(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		double lastExeValue = mbo.getDouble("lastexevalue");
		double frequency = mbo.getDouble("frequency");
		double nextValue = 0.0;
		nextValue = lastExeValue + frequency;
		mbo.setValue("nextvalue", nextValue, 11L);
	}

}
