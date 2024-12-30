package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldGpmActionLast extends MboValueAdapter {

	public FldGpmActionLast(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		double actionlast = mbo.getDouble("actionlast");
		double actionfrq = mbo.getDouble("actionfrq");
		
		double actionnext = actionlast + actionfrq;
		mbo.setValue("actionnext", actionnext, 11L);// 下一次 = 上一次 + 频率
	}
}
