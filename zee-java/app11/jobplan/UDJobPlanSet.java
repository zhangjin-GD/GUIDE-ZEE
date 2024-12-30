package guide.app.jobplan;

import java.rmi.RemoteException;

import psdi.app.jobplan.JobPlanSet;
import psdi.app.jobplan.JobPlanSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDJobPlanSet extends JobPlanSet implements JobPlanSetRemote {

	public UDJobPlanSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet mboSet) throws MXException, RemoteException {
		return new UDJobPlan(mboSet);
	}
	
}
