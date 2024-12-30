package guide.webclient.system.beans;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.workflow.WFInstanceRemote;
import psdi.workflow.WFInstanceSetRemote;

public class UDWorkflowStopDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if(hasWfComplete(mbo)){
			throw new MXApplicationException("guide", "1085");//已审批？
		}
		String message = mbo.getMessage("guide", "1087");
		WFInstanceSetRemote wfInstanceSet = (WFInstanceSetRemote) mbo.getMboSet("$WORKFLOWSTOP", "WFINSTANCE",
				"active=1 and ownertable='"+mbo.getName()+"' and ownerid="+mbo.getUniqueIDValue());
		if(!wfInstanceSet.isEmpty() && wfInstanceSet.count() > 0){
			if(!wfInstanceSet.getMbo(0).getString("originator").equalsIgnoreCase(mbo.getUserInfo().getPersonId())){
				throw new MXApplicationException("guide", "1086");//发起人？
			}
			WFInstanceRemote wfInstance = (WFInstanceRemote) wfInstanceSet.getMbo(0);
			wfInstance.stopWorkflow("STOP");
			message = mbo.getMessage("guide", "1088");
		}
		setOriStatus(mbo);
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", message, 1);
		return super.execute();
	}

	private void setOriStatus(MboRemote mbo) throws RemoteException, MXException {
		String objectName = mbo.getName();
		String statusName = "";
		if(objectName.equalsIgnoreCase("PR")){
			statusName = "PRSTATUS";
		}else if(objectName.equalsIgnoreCase("PO")){
			statusName = "POSTATUS";
		}else if(objectName.equalsIgnoreCase("INVUSE")){
			statusName = "INVUSESTATUS";
		}else if(objectName.equalsIgnoreCase("WORKORDER")){
			statusName = "WOSTATUS";
		}else if(objectName.equalsIgnoreCase("RFQ")){
			statusName = "RFQSTAT";
		}
		if(statusName != null && !statusName.equalsIgnoreCase("")){
			MboSetRemote statusSet = mbo.getMboSet(statusName);
			statusSet.setOrderBy("changedate asc");
			if(!statusSet.isEmpty() && statusSet.count() > 0){
				mbo.setValue("status", statusSet.getMbo(0).getString("status"), 11L);
			}
		}
	}

	private boolean hasWfComplete(MboRemote mbo) throws RemoteException, MXException {
		MboSetRemote wfassignmentSet = mbo.getMboSet("$WFASSIGNMENTCOMPLETE", "WFASSIGNMENT",
				"assignstatus='COMPLETE' and ownertable='"+mbo.getName()+"' and ownerid="+mbo.getUniqueIDValue());
		if(!wfassignmentSet.isEmpty() && wfassignmentSet.count() > 0){
			return true;
		}
		return false;
	}
	
	
}
