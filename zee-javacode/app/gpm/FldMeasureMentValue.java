package guide.app.gpm;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.asset.MeasurePoint;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMeasureMentValue extends MboValueAdapter {

	public FldMeasureMentValue(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		if (parent != null && parent instanceof MeasurePoint) {
			String readingtype = parent.getString("READINGTYPE");
			parent.getMboSet("UDMEASUREMENT").resetQbe();
			double value = 0;
			if ("DELTA".equalsIgnoreCase(readingtype)) {
				value = mbo.getThisMboSet().sum("value");
			} else {
				MboSetRemote mboSet = mbo.getThisMboSet();
				if (!mboSet.isEmpty() && mboSet.count() > 0) {
					long newdate = 0;
					for (int i = 0; mboSet.getMbo(i) != null; i++) {
						MboRemote mbothis = mboSet.getMbo(i);
						Date measuredate = mbothis.getDate("measuredate");
						long measureTime = measuredate.getTime();
						if (measureTime > newdate) {
							value = mbothis.getDouble("value");
							newdate = measureTime;
						}
					}
					value = Math.abs(value);
				}
			}
			parent.setValue("value", value, 2L);
		}
	}
}
