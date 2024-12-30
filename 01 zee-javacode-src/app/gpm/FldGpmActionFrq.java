package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldGpmActionFrq extends MboValueAdapter {

	public FldGpmActionFrq(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		double serialnum = mbo.getDouble("serialnum");
		double actionlast = mbo.getDouble("actionlast");
		double actionfrq = mbo.getDouble("actionfrq");

		double actiontarget = serialnum * actionfrq;
		double actionnext = actionlast + actionfrq;
		mbo.setValue("actiontarget", actiontarget, 11L);// 目标值=序号 * 频率
		mbo.setValue("actionnext", actionnext, 11L);// 下一次 = 上一次 + 频率
	}
}
