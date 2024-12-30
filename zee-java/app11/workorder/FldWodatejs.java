package guide.app.workorder;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldWodatejs extends MboValueAdapter {

	public FldWodatejs(MboValue mbv) throws MXException {
		super(mbv);
	}

	public void action() throws RemoteException, MXException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		Date starttime = mbo.getDate("STARTTIME");
		Date endtime = mbo.getDate("ENDTIME");
		if (starttime != null && endtime != null) {

			if ((starttime.getTime() > endtime.getTime())) {
				throw new MXApplicationException("guide", "1072");
			}

			Long date = (endtime.getTime() - starttime.getTime()) / 1000 / 60;
			int intValue = date.intValue();
			BigDecimal b = new BigDecimal((double) intValue / 60);
			Double hour = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

			mbo.setValue("WORKTIME", hour, 2L);

		}
	}
}
