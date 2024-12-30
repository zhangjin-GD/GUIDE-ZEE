package guide.webclient.beans.workplan;

import java.rmi.RemoteException;

import guide.app.workorder.UDWO;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.workflow.WorkFlowServiceRemote;

public class WPWorkOrderTableBean extends DataBean {

	public int autoInitWorkflow() throws RemoteException, MXException {
		MboRemote mbo = this.getMbo();
		if (mbo != null && !mbo.isNull("wonum")) {
			String wonum = mbo.getString("wonum");
			MboSetRemote woSet = mbo.getMboSet("$WORKORDER", "workorder", "wonum = '" + wonum + "'");
			if (!woSet.isEmpty() && woSet.count() > 0) {
				UDWO wo = (UDWO) woSet.getMbo(0);
				String worktype = wo.getString("worktype");
				String processname = "";
				if ("CM".equalsIgnoreCase(worktype)) {
					processname = "UDWOCM";
				} else if ("PM".equalsIgnoreCase(worktype) || "IM".equalsIgnoreCase(worktype)) {
					processname = "UDWOPM";
				} else if ("EM".equalsIgnoreCase(worktype)) {
					processname = "UDWOEM";
				} else if ("FM".equalsIgnoreCase(worktype)) {
					processname = "UDWOFM";
				} else if ("SM".equalsIgnoreCase(worktype)) {
					processname = "UDWOSM";
				}
				initWorkflow(wo, processname);
			}
		}
		this.app.getAppBean().save();
		return 1;
	}

	private void initWorkflow(MboRemote mbo, String processname) throws RemoteException, MXException {
		WorkFlowServiceRemote wfServiceRemote = (WorkFlowServiceRemote) MXServer.getMXServer().lookup("WORKFLOW");
		boolean isEnable = wfServiceRemote.isActiveProcess(processname, "WORKORDER", mbo.getUserInfo());
		if (isEnable && wfServiceRemote.getActiveInstances(mbo).isEmpty()) {
			wfServiceRemote.initiateWorkflow(processname, mbo);
		}
	}

}
