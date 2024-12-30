package guide.webclient.system.beans;

import java.rmi.RemoteException;

import psdi.webclient.system.beans.WorkflowBean;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.NonPersistentMboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.controller.SessionContext;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.webclient.system.runtime.WebClientRuntime;
import psdi.webclient.system.session.WebClientSession;
import psdi.workflow.DirectorInput;
import psdi.workflow.WFAssignmentRemote;
import psdi.workflow.WFInstanceRemote;
import psdi.workflow.WorkflowDirector;

public class UDWorkflowBean extends WorkflowBean {
	WorkflowDirector director;
	MboSetRemote workflowSet;

	public void setupBean(WebClientSession wcs) {
		super.setupBean(wcs);
		this.director = this.clientSession.getWorkflowDirector();
		this.workflowSet = this.director.getWfSet();
	}

	/** @deprecated */
	public void setupBean(SessionContext sc) {
		setupBean(sc.getMasterInstance());
	}

	protected void pageCheck() throws MXException {
		if (this.director != null) {
			getWorkflowDirections(this.director);
		}
	}

	public synchronized int execute() throws MXException, RemoteException {
		try {
			if (mboSetRemote != null
					&& (mboSetRemote instanceof NonPersistentMboSetRemote))
				((NonPersistentMboSetRemote) mboSetRemote).execute();
			director.input(DirectorInput.ok);
			pageCheck();
			return 1;
		} catch (MXException mxe) {
			clientSession.showMessageBox(mxe);
		}
		WebClientRuntime.sendEvent(new WebClientEvent("dialogclose", app
				.getCurrentPageId(), null, clientSession));
		clientSession.addMXWarnings(director.getWorkflowWarnings());
		app.getAppBean().reset();
		app.getAppBean().setCurrentRow(0);
		director.reset();
		parent.getMbo().getThisMboSet().reset();
		fireDataChangedEvent();
		return 0;
	}

	public int executeReturn() throws MXException, RemoteException {
		try {
			if ((this.mboSetRemote != null)
					&& ((this.mboSetRemote instanceof NonPersistentMboSetRemote))) {
				((NonPersistentMboSetRemote) this.mboSetRemote).execute();
			}
			this.director.input(DirectorInput.ok);
			pageCheck();

			// 获取系统session实例
			WebClientSession wcs = sessionContext.getMasterInstance();
			// 构建跳转至启动中心的URL
			String url = "?event=loadapp&value=startcntr";
			// 跳转动作执行
			wcs.gotoApplink(url);

			return 1;
		} catch (MXException mxe) {
			this.clientSession.showMessageBox(mxe);

			WebClientRuntime.sendEvent(new WebClientEvent("dialogclose",
					this.app.getCurrentPageId(), null, this.clientSession));
			this.clientSession.addMXWarnings(this.director
					.getWorkflowWarnings());

			this.app.getAppBean().reset();
			this.app.getAppBean().setCurrentRow(0);
			this.director.reset();
			this.parent.getMbo().getThisMboSet().reset();
			fireDataChangedEvent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public int executeNext() throws MXException, RemoteException {
		try {
			String nextUrl = getNextWfassign("assignstatus='ACTIVE' and assigncode='"+getMbo().getUserInfo().getPersonId()+"' and ownerid!="+app.getAppBean().getMbo().getUniqueIDValue()+"");
			
			if ((this.mboSetRemote != null)
					&& ((this.mboSetRemote instanceof NonPersistentMboSetRemote))) {
				((NonPersistentMboSetRemote) this.mboSetRemote).execute();
			}
			this.director.input(DirectorInput.ok);
			pageCheck();
			
			// 获取系统session实例
			WebClientSession wcs = sessionContext.getMasterInstance();
			// 跳转动作执行
			wcs.gotoApplink(nextUrl);

			return 1;
		} catch (MXException mxe) {
			this.clientSession.showMessageBox(mxe);
			WebClientRuntime.sendEvent(new WebClientEvent("dialogclose",this.app.getCurrentPageId(), null, this.clientSession));
			this.clientSession.addMXWarnings(this.director.getWorkflowWarnings());
			this.app.getAppBean().reset();
			this.app.getAppBean().setCurrentRow(0);
			this.director.reset();
			this.parent.getMbo().getThisMboSet().reset();
			fireDataChangedEvent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private String getNextWfassign(String sql) throws RemoteException, MXException {
		String url = "?event=loadapp&value=startcntr";
		MboSetRemote wfassignmentSet = MXServer.getMXServer().getMboSet("WFASSIGNMENT", MXServer.getMXServer().getSystemUserInfo());
		wfassignmentSet.setWhere(sql);
		wfassignmentSet.setOrderBy("duedate desc");
		if(!wfassignmentSet.isEmpty() && wfassignmentSet.count() > 0){
			MboRemote wfassignment = wfassignmentSet.getMbo(0);
			url = "?event=loadapp&value="+wfassignment.getString("app")+"&uniqueid="+wfassignment.getInt("ownerid")+"";
		}
		wfassignmentSet.close();
		return url;
	}

	protected MboSetRemote getMboSetRemote() throws MXException,
			RemoteException {
		return this.workflowSet;
	}

	public int selectrecord() throws MXException, RemoteException {
		WebClientEvent event = this.clientSession.getCurrentEvent();
		int row = getRowIndexFromEvent(event);
		MboRemote mboRemote = this.mboSetRemote.getMbo(row);
		if (mboRemote != null)
			mboRemote.select();
		execute();
		WebClientRuntime.sendEvent(new WebClientEvent("dialogclose", this.app
				.getCurrentPageId(), null, this.clientSession));
		return 1;
	}

	public int cancelDialog() throws MXException, RemoteException {
		this.director.input(DirectorInput.cancel);
		WebClientRuntime.sendEvent(new WebClientEvent("dialogclose", this.app
				.getCurrentPageId(), null, this.clientSession));
		this.clientSession.addMXWarnings(this.director.getWorkflowWarnings());
		this.app.getAppBean().reset();
		this.app.getAppBean().setCurrentRow(0);
		return 1;
	}

	public int directorinput() throws MXException, RemoteException {
		WebClientEvent event = this.clientSession.getCurrentEvent();
		if (event != null) {
			String value = event.getValueString();
			if (value != null) {
				if (value.equals("reassign")) {
					this.director.input(DirectorInput.reassign);
				} else if (value.equals("reassignfromlist")) {
					int row = getRowIndexFromEvent(event);
					MboRemote mboRemote = this.mboSetRemote.getMbo(row);
					if (mboRemote != null) {
						mboRemote.select();
					}
					this.director.setAssignment((WFAssignmentRemote) mboRemote);
					this.director.input(DirectorInput.reassign);
				} else if (value.equals("list")) {
					this.director.input(DirectorInput.list);
				} else if (value.equals("initiate")) {
					this.director.input(DirectorInput.initiate);
				} else if (value.equals("complete")) {
					int row = getRowIndexFromEvent(event);
					MboRemote mboRemote = this.mboSetRemote.getMbo(row);
					if (mboRemote != null) {
						mboRemote.select();
					}
					this.director.setAssignment((WFAssignmentRemote) mboRemote);
					this.director.input(DirectorInput.complete);
				} else if (value.equals("stopinstance")) {
					int row = getRowIndexFromEvent(event);
					MboRemote mboRemote = this.mboSetRemote.getMbo(row);
					if (mboRemote != null) {
						mboRemote.select();
					}
					this.director.setInstance((WFInstanceRemote) mboRemote);
					this.director.input(DirectorInput.stopinstance);
				}

				pageCheck();
				WebClientRuntime.sendEvent(new WebClientEvent("dialogclose",
						this.app.getCurrentPageId(), null, this.clientSession));
			}
		}
		return 1;
	}

	protected void getWorkflowDirections(WorkflowDirector wfdirector)
			throws MXException {
		getParent().reloadTable();
		super.getWorkflowDirections(wfdirector);
	}
}
