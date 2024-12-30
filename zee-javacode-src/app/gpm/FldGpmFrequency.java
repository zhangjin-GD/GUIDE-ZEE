package guide.app.gpm;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldGpmFrequency extends MboValueAdapter {

	public FldGpmFrequency(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		boolean usetargetdate = mbo.getBoolean("usetargetdate");
		if (usetargetdate) {
			Date laststartdate = mbo.getDate("laststartdate");
			if (laststartdate != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(laststartdate);
				calendar.add(Calendar.DATE, mbo.getInt("frequency"));
				mbo.setValue("nextdate", calendar.getTime(), 11L);
			}
		} else {
			Date lastcompdate = mbo.getDate("lastcompdate");
			if (lastcompdate != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(lastcompdate);
				calendar.add(Calendar.DATE, mbo.getInt("frequency"));
				mbo.setValue("nextdate", calendar.getTime(), 11L);
			}
		}

	}

}
