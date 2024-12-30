package guide.app.jobplan;

import java.rmi.RemoteException;

import psdi.app.person.FldPersonID;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldJobPlanLead extends FldPersonID {

	public FldJobPlanLead(MboValue mbv) throws MXException {
		super(mbv);
	}
	
	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("personid in(select resppartygroup from persongroupteam where persongroup=:persongroup)");
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}
}
