package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.app.ticket.WorkLog;
import psdi.app.ticket.WorkLogSet;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSet;
import psdi.mbo.custapp.NonPersistentCustomMboSetRemote;
import psdi.util.MXException;

public class VWOSet extends NonPersistentCustomMboSet implements NonPersistentCustomMboSetRemote {

	public VWOSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {

		return new VWO(ms);
	}

	@Override
	public void execute() throws MXException, RemoteException {
		super.execute();

		MboRemote owner = this.getOwner();
		if (owner != null && owner instanceof UDWO) {
			String description = this.getString("description");
			WorkLogSet workLogSet = (WorkLogSet) owner.getMboSet("WORKLOG");
			WorkLog workLog = (WorkLog) workLogSet.add(11L);
			workLog.setValue("description", description, 11L);
		}
	}
}
