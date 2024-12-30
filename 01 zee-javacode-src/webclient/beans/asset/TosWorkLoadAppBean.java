package guide.webclient.beans.asset;

import java.rmi.RemoteException;
import java.util.Hashtable;

import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.beans.ResultsBean;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.webclient.system.session.WebClientSession;

public class TosWorkLoadAppBean extends AppBean {

	protected Hashtable<String, String> qbeAttributesBeforeReviseAction = null;
	protected boolean saveFetchData = false;

	public Hashtable<String, String> getQbeAttributesBeforeReviseAction() {
		return this.qbeAttributesBeforeReviseAction;
	}

	protected synchronized void setQbeAttributesBeforeReviseAction(Hashtable<String, String> oldQbeAttributes) {
		if (oldQbeAttributes != null && !oldQbeAttributes.isEmpty()) {
			this.qbeAttributesBeforeReviseAction = new Hashtable(oldQbeAttributes);
		}

	}

	// 2533XOCT
	public int AttributesSearch() throws RemoteException, MXException {
		DataBean results = this.getResultsBean();
		MboSetRemote rmboset = results.getMboSet();
		this.app.setQueryCancelMboset(rmboset);
		this.app.setQueryCancelResultsBean((DataBean) results);
		if (results instanceof ResultsBean) {
			((ResultsBean) results).setResetFromQbeclearInit(false);
		}

		this.setFilterCleared();
		((DataBean) results).resetQbe();
		this.checkQuery((DataBean) results);
		WebClientSession.checkResults((DataBean) results, this.clientSession, this.app);
		return 1;
	}

	public void checkQuery(DataBean results) throws MXException, RemoteException {
		results.setQbeAttributes(this.getQbeAttributes());
		results.reset();
		
		try {
			results.getMbo(0);
			results.saveCurrentQbeSettings(true);
			results.setQueryNameBeforeReviseAction("");
			results.setQueryDescBeforeReviseAction("");
			this.qbeAttributesBeforeReviseAction = null;

		} catch (MXException var3) {
			results.resetQbe();
			results.saveCurrentQbeSettings(true);
			results.setQbeAttributes(this.getQbeAttributesBeforeReviseAction());
			results.reset();
			if (!this.saveFetchData) {
				results.setTableFlag(256L, false);
				results.setTableFlag(8192L, true);
				results.turnEmptyStateOn();
			}

			this.clientSession
					.queueEvent(new WebClientEvent("searchmore", this.app.getId(), (Object) null, this.clientSession));
			throw var3;
		}
	}
}
