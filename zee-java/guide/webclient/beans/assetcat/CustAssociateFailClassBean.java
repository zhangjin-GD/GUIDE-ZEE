package guide.webclient.beans.assetcat;

import java.rmi.RemoteException;

import guide.app.asset.FailClassSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.beans.common.TreeControlBean;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.Utility;
import psdi.webclient.system.controller.WebClientEvent;

public class CustAssociateFailClassBean extends TreeControlBean {

	private DataBean originalBean = null;
	private FailClassSet classStructSet = null;
	private boolean isfrominitialize = false;

	public void initialize() throws MXException, RemoteException {
		this.isfrominitialize = true;
		super.initialize();
		ControlInstance originalControl = this.creatingEvent.getSourceControlInstance();
		this.originalBean = this.clientSession.getDataBean(originalControl.getProperty("datasrc"));
		this.classStructSet = (FailClassSet) this.getMboSet();

		this.classStructSet.setOriginatingObject(this.originalBean.getMbo());
		if (this.classStructSet.getApp() == null) {
			String appName = this.app.getId();
			if (appName != null) {
				if ("udwocm".equalsIgnoreCase(appName) || "udwoem".equalsIgnoreCase(appName)) {
					MboRemote mbo = this.originalBean.getMbo();
					String udassettypecode = mbo.getString("udassettypecode");
					this.classStructSet.setAppWhere("type = '" + udassettypecode + "'");
				}
				this.classStructSet.setIsLookup(true);
				this.classStructSet.setApp(appName.toUpperCase());
			}
		}
	}

	public int selectrecord() throws MXException {
		WebClientEvent event = this.sessionContext.getCurrentEvent();

		try {
			super.selectrecord();
			this.updateOriginatingRecord();
		} catch (MXException var3) {
			Utility.sendEvent(
					new WebClientEvent("dialogclose", this.app.getCurrentPageId(), (Object) null, this.sessionContext));
			Utility.showMessageBox(event, var3);
		} catch (RemoteException var4) {
			Utility.sendEvent(
					new WebClientEvent("dialogclose", this.app.getCurrentPageId(), (Object) null, this.sessionContext));
			Utility.showMessageBox(event, var4);
		}

		return 1;
	}

	protected void updateOriginatingRecord() throws MXException, RemoteException {
		String uniqueIdSelected = this.sessionContext.getCurrentEvent().getValueString();
		MboSetRemote originalSet = this.originalBean.getMboSet();

		MboRemote selectedClassMbo = this.getMbo();
		if (selectedClassMbo == null) {
			MXServer server = MXServer.getMXServer();
			SqlFormat sqf = new SqlFormat(server.getSystemUserInfo(), "udfailclassid=:1");
			sqf.setObject(1, "UDFAILCLASS", "udfailclassid", uniqueIdSelected);
			MboSetRemote failclassSet = server.getMboSet("UDFAILCLASS", server.getSystemUserInfo());
			failclassSet.setWhere(sqf.format());
			failclassSet.reset();
			selectedClassMbo = failclassSet.getMbo(0);
		}

		MboRemote originalRecord = originalSet.getMbo();
		if (originalRecord == null) {
			originalRecord = originalSet.getMbo(0);
		}

		if (originalRecord != null && selectedClassMbo != null) {
			this.originalBean.setValue("udfailmech", selectedClassMbo.getString("failclassnum"), 2L);
			Utility.sendEvent(
					new WebClientEvent("dialogclose", this.app.getCurrentPageId(), (Object) null, this.sessionContext));
		}
	}

	protected synchronized boolean moveTo(int row) throws MXException, RemoteException {
		if (row == 0 && this.isfrominitialize) {
			this.isfrominitialize = false;
			return true;
		} else {
			return super.moveTo(row);
		}
	}
}
