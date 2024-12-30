package guide.app.asset;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

import java.rmi.RemoteException;
import java.util.Date;

public class FldDateJy extends MboValueAdapter {
	public FldDateJy(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void validate() throws MXException, RemoteException {
		super.validate();

		MboRemote mbo = getMboValue().getMbo();
		String actstarttimeStr = mbo.getString("actstarttime");
		String actendtimeStr = mbo.getString("actendtime");
		Date actstarttime = mbo.getDate("actstarttime");
		Date actendtime = mbo.getDate("actendtime");

		String appname = mbo.getThisMboSet().getApp();
		if ("udassetstopc".equalsIgnoreCase(appname)) {
			if (actstarttimeStr != null && actendtimeStr != null && (actstarttime.getTime() > actendtime.getTime())) {
				throw new MXApplicationException("guide", "1072");
			}
		}
	}
}
