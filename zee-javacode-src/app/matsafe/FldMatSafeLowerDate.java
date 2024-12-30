package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatSafeLowerDate extends MboValueAdapter{
	
	public FldMatSafeLowerDate(MboValue mbovalue) {
		super(mbovalue);
	}
	
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		if (!this.getMboValue().isNull()) {
			mbo.setValue("status", "INACTIVE", 11L);
		} else {
			mbo.setValue("status", "RUN", 11L);
		}
	}
}
