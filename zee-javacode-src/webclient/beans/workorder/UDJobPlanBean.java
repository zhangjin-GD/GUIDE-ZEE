package guide.webclient.beans.workorder;

import guide.webclient.beans.system.UDLookupBean;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class UDJobPlanBean extends UDLookupBean{

	public int REFRESHLIST() throws MXException, RemoteException {
		MboSetRemote jpSet = getMboSetRemote();
		MboRemote woOwner = jpSet.getOwner();
		String worktype = woOwner.getString("worktype");
		String ofs = woOwner.getString("udofs");
		jpSet.setWhere("udworktype = '"+ worktype +"' and udofs='"+ ofs +"'");
		jpSet.reset();
		refreshTable();
		structureChangedEvent(this);
		fireDataChangedEvent(this);
		sessionContext.queueRefreshEvent();
		getMboSetRemote();
		app.getAppBean().fireDataChangedEvent(app.getAppBean());
		return 1;
	}
}
