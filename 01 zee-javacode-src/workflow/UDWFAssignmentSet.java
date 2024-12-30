package guide.workflow;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;
import psdi.workflow.WFAssignmentSet;
import psdi.workflow.WFAssignmentSetRemote;

public class UDWFAssignmentSet extends WFAssignmentSet implements WFAssignmentSetRemote {

	public UDWFAssignmentSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {

		return new UDWFAssignment(ms);
	}
}
