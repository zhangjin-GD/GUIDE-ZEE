package guide.app.jobplan;

import java.rmi.RemoteException;

import psdi.app.jobplan.JobPlan;
import psdi.app.jobplan.JobPlanRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDJobPlan extends JobPlan implements JobPlanRemote {

	public UDJobPlan(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		setValue("udcreateby", getUserInfo().getPersonId(), 2L);//创建人
		setValue("udcreatetime", MXServer.getMXServer().getDate(), 11L);//创建时间
	}
	
	@Override
	public void save() throws MXException, RemoteException {
		super.save();
	}
	
}
