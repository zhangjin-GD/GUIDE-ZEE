package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatSafeRunInit extends MboValueAdapter {

	public FldMatSafeRunInit(MboValue mbovalue) {
		super(mbovalue);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double init_1 = mbo.getMboValue("runinit").getPreviousValue().asDouble();
		double init_2 = mbo.getDouble("runinit");
		double runact = mbo.getDouble("runact");
		double init = init_2 - init_1;
		runact = runact + init;
		mbo.setValue("runact", runact, 11L);
	}
}
