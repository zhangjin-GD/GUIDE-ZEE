package guide.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldHangDur extends MboValueAdapter{
	
	public FldHangDur(MboValue mbv) {
		super(mbv);
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote wo = mbo.getOwner();
		double hours = 0;
		double nh = 1000 * 60 * 60; 
		if (wo != null) {
			double hangdur = wo.getDouble("udhangdur");
			
			Date starttime = mbo.getDate("starttime");
			Date endtime = mbo.getDate("endtime");
			hours = (endtime.getTime() - starttime.getTime())/nh;
			hangdur += hours;
			wo.setValue("udhangdur", hangdur,11L);
		}
		super.action();
	}
}
