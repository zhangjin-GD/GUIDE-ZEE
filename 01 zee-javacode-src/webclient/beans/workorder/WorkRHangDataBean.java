package guide.webclient.beans.workorder;

import guide.app.workorder.UDWO;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class WorkRHangDataBean extends DataBean{
	
	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.getMbo();
		mbo.setValue("endtime", mbo.getDate("vendtime"),2L);
		UDWO owner = (UDWO) mbo.getOwner();
		if (owner != null) {
//			owner.changeStatus(mbo.getString("oldstatus"), MXServer.getMXServer().getDate(), "HANG");
			owner.setValue("UDWOSTATUS", mbo.getString("oldstatus"), 11L);
		}
		this.getMboSet().save();
		this.app.getAppBean().save();
		return super.execute();
	}
}
