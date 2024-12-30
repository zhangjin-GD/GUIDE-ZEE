package guide.app.security;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldUseTime extends MboValueAdapter {

	public FldUseTime(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		MboRemote mbo = getMboValue().getMbo();
		double nh = 1000 * 60 * 60;
		Date startTime = mbo.getDate("starttime");
		Date endTime = mbo.getDate("endtime");
		if (startTime != null && endTime != null) {
			int result = startTime.compareTo(endTime);
			if (result > 0) {
				throw new MXApplicationException("guide", "1036");
			} else {
				double diff = endTime.getTime() - startTime.getTime();
				double hour = diff / nh;
				mbo.setValue("usetime", hour, 11L);
			}
		}
	}
}
