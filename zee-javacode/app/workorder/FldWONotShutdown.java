package guide.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWONotShutdown extends MboValueAdapter {

	public FldWONotShutdown(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		boolean notshutdown = this.getMboValue().getBoolean();
		if (notshutdown) {
			mbo.setValue("udfaildur", 0, 11L);
		} else {
			if (!mbo.isNull("actfinish")) {
				Date actfinish = mbo.getDate("actfinish");
				long actTime = actfinish.getTime() + 1;
				mbo.setValue("actfinish", new Date(actTime), 2L);
			}
		}
	}
}
