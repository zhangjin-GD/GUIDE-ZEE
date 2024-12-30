package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPrIsSs extends MboValueAdapter {
	public FldPrIsSs(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void init() throws MXException, RemoteException {
		super.init();
		Mbo mbo = this.getMboValue().getMbo();
		if (mbo.getBoolean("udisss")) {
			mbo.setFieldFlag("udremark", 128L, true);
		} else {
			mbo.setFieldFlag("udremark", 128L, false);
		}
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		if (mbo.getBoolean("udisss")) {
			mbo.setFieldFlag("udremark", 128L, true);
		} else {
			mbo.setFieldFlag("udremark", 128L, false);
		}
	}

}
