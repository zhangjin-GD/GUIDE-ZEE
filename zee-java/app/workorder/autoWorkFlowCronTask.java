package guide.app.workorder;


import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;
import psdi.workflow.WorkFlowServiceRemote;

public class autoWorkFlowCronTask extends SimpleCronTask {

	@Override
	public void cronAction() {
		try {
			String objectName = getParamAsString("objectName");
			String sqlWhere = getParamAsString("sqlWhere");
			String personName = getParamAsString("personName");
			String processName = getParamAsString("processName");
			MXServer server = MXServer.getMXServer();
			MboSetRemote objectSet = server.getMboSet(objectName, server.getSystemUserInfo());
			objectSet.setWhere(sqlWhere);
			if (!objectSet.isEmpty() && objectSet.count() > 0) {
				int count = objectSet.count();
				MboRemote object = null;
				for (int i = 0; count > i; i++) {
					object = objectSet.getMbo(0);
					WorkFlowServiceRemote serviceRemote = (WorkFlowServiceRemote) MXServer.getMXServer().lookup("WORKFLOW");
					boolean isEnabled = serviceRemote.isActiveProcess(processName, objectName, server.getUserInfo(object.getString(personName)));
					if (isEnabled && serviceRemote.getActiveInstances(object).isEmpty()) {
						try {
							serviceRemote.initiateWorkflow(processName, object);
						} catch (Exception e) {
							continue;
						}
					}
				}
			}
			objectSet.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}

	}

}
