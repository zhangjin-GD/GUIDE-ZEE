package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldGpmSerialNum extends MboValueAdapter {

	public FldGpmSerialNum(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		double serialnum = mbo.getDouble("serialnum");
		double actionfrq = mbo.getDouble("actionfrq");
		
		double nextValue = serialnum * actionfrq;
		mbo.setValue("actiontarget", nextValue, 11L);
	}
}
