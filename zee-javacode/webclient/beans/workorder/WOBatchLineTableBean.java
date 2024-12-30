package guide.webclient.beans.workorder;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.workflow.WFActionRemote;
import psdi.workflow.WFActionSetRemote;
import psdi.workflow.WFAssignmentRemote;
import psdi.workflow.WFAssignmentSetRemote;
import psdi.workflow.WFInstanceRemote;

public class WOBatchLineTableBean extends DataBean {

	public int addwo() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		// 调用dialog
		this.clientSession.loadDialog("addwo");
		return 1;
	}

	public void updatewf() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}

		String createby = owner.getString("createby");
		MboSetRemote lineSet = this.getMboSet();
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			for (int i = 0; lineSet.getMbo(i) != null; i++) {
				MboRemote line = lineSet.getMbo(i);
				MboSetRemote woSet = line.getMboSet("workorder");
				if (!woSet.isEmpty() && woSet.count() > 0) {
					MboRemote wo = woSet.getMbo(0);
					WFAssignmentSetRemote wfmentSet = (WFAssignmentSetRemote) wo.getMboSet("WFASSIGNMENT");
					wfmentSet.setWhere("ASSIGNCODE='" + createby + "'");
					if (!wfmentSet.isEmpty() && wfmentSet.count() > 0) {
						WFAssignmentRemote assignment = (WFAssignmentRemote) wfmentSet.getMbo(0);
						WFInstanceRemote instance = assignment.getWFInstance();
						String assignID = assignment.getString("ASSIGNID");
						WFActionSetRemote wfActionSet = (WFActionSetRemote) assignment.getMboSet("ACTIONS");
						WFActionRemote wfAction = wfActionSet.getAction(true);
						int actionID = wfAction.getInt("ACTIONID");
						instance.completeWorkflowAssignment(assignID, actionID, "批量处理工单");
					} else {
						throw new MXApplicationException("workflow", "SpecificNothing", new String[] { "UDWOPM" });
					}
				}
			}
		}
	}

	public void updatewo() throws RemoteException, MXException {

		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		Date actstart = owner.getDate("actstart");
		Date actfinish = owner.getDate("actfinish");
		String failanalysis = owner.getString("failanalysis");
		if (actstart == null) {
			throw new MXApplicationException("guide", "1111");
		}
		if (actfinish == null) {
			throw new MXApplicationException("guide", "1112");
		}
		MboSetRemote lineSet = this.getMboSet();
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			for (int i = 0; lineSet.getMbo(i) != null; i++) {
				MboRemote line = lineSet.getMbo(i);
				MboSetRemote woSet = line.getMboSet("workorder");
				if (!woSet.isEmpty() && woSet.count() > 0) {
					MboRemote wo = woSet.getMbo(0);
					if (wo.isNull("actstart")) {
						wo.setValue("actstart", actstart, 2L);
					}
					if (wo.isNull("actfinish")) {
						wo.setValue("actfinish", actfinish, 2L);
					}
					if (wo.isNull("udfailanalysis")) {
						wo.setValue("udfailanalysis", failanalysis, 11L);
					}
				}
			}
		}
		this.app.getAppBean().save();
	}

}
