package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatSafeActionInit extends MboValueAdapter {

	public FldMatSafeActionInit(MboValue mbovalue) {
		super(mbovalue);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double init_1 = mbo.getMboValue("actioninit").getPreviousValue().asDouble();
		double init_2 = mbo.getDouble("actioninit");
		double actionact = mbo.getDouble("actionact");
		double init = init_2 - init_1;
		actionact = actionact + init;
		mbo.setValue("actionact", actionact, 11L);
	}
}
