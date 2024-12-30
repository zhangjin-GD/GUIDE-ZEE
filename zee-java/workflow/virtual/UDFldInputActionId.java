package guide.workflow.virtual;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
import psdi.workflow.virtual.FldInputActionId;

public class UDFldInputActionId extends FldInputActionId {

	public UDFldInputActionId(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void init() throws MXException, RemoteException {
		super.init();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote wfInstance = mbo.getOwner();// WFInstance
		String processName = wfInstance.getString("processname");
		String ownerTable = wfInstance.getString("ownertable");
		long ownerid = wfInstance.getLong("ownerid");
		String sql1 = "processname='" + processName + "' and ownerid ='" + ownerid + "' and ownerTable ='" + ownerTable
				+ "' and assignstatus='ACTIVE'";
		MboSetRemote wfassignmentSet = mbo.getMboSet("$wfassignment", "wfassignment", sql1);
		if (!wfassignmentSet.isEmpty() && wfassignmentSet.count() > 0) {
			MboRemote wfassignment = wfassignmentSet.getMbo(0);
			int processrev = wfassignment.getInt("processrev");
			int actionId = mbo.getInt("actionid");
			String sql2 = "processname='" + processName + "' and processrev=" + processrev + " and actionid="
					+ actionId;
			MboSetRemote wfactionSet = mbo.getMboSet("$WFACTION", "WFACTION", sql2);
			if (!wfactionSet.isEmpty() && wfactionSet.count() > 0) {
				String instruction = wfactionSet.getMbo(0).getString("instruction");
				mbo.setValue("memo", instruction, 11L);
			}
		}
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote wfInstance = mbo.getOwner();// WFInstance
		String processName = wfInstance.getString("processname");
		String ownerTable = wfInstance.getString("ownertable");
		long ownerid = wfInstance.getLong("ownerid");
		String sql1 = "processname='" + processName + "' and ownerid ='" + ownerid + "' and ownerTable ='" + ownerTable
				+ "' and assignstatus='ACTIVE'";
		MboSetRemote wfassignmentSet = mbo.getMboSet("$wfassignment", "wfassignment", sql1);
		if (!wfassignmentSet.isEmpty() && wfassignmentSet.count() > 0) {
			MboRemote wfassignment = wfassignmentSet.getMbo(0);
			int processrev = wfassignment.getInt("processrev");
			int actionId = mbo.getInt("actionid");
			String sql2 = "processname='" + processName + "' and processrev=" + processrev + " and actionid="
					+ actionId;
			MboSetRemote wfactionSet = mbo.getMboSet("$WFACTION", "WFACTION", sql2);
			if (!wfactionSet.isEmpty() && wfactionSet.count() > 0) {
				String instruction = wfactionSet.getMbo(0).getString("instruction");
				mbo.setValue("memo", instruction, 11L);
			}
		}
	}
}
