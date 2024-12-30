package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatSafeTeuInit extends MboValueAdapter {

	public FldMatSafeTeuInit(MboValue mbovalue) {
		super(mbovalue);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double init_1 = mbo.getMboValue("teuinit").getPreviousValue().asDouble();
		double init_2 = mbo.getDouble("teuinit");
		double teuact = mbo.getDouble("teuact");
		double init = init_2 - init_1;
		teuact = teuact + init;
		mbo.setValue("teuact", teuact, 11L);
	}
}
