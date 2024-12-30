package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatSafeUnitInit extends MboValueAdapter {

	public FldMatSafeUnitInit(MboValue mbovalue) {
		super(mbovalue);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double init_1 = mbo.getMboValue("unitinit").getPreviousValue().asDouble();
		double init_2 = mbo.getDouble("unitinit");
		double unitact = mbo.getDouble("unitact");
		double init = init_2 - init_1;
		unitact = unitact + init;
		mbo.setValue("unitact", unitact, 11L);
	}
}
