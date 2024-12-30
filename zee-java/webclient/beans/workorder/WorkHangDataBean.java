package guide.webclient.beans.workorder;

import guide.app.workorder.UDWO;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class WorkHangDataBean extends DataBean{

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		UDWO wo = (UDWO) this.getMbo();
		String wostatus = wo.getString("udwostatus");
		if (!wostatus.equals("HANG") &&  wo.getInternalStatus().equalsIgnoreCase("INPRG")) {
			MboSetRemote hangSet = wo.getMboSet("UDWOHANG");
			MboRemote hang = hangSet.add();
			hang.setValue("wonum", wo.getString("wonum"),11L);
			hang.setValue("assetnum", wo.getString("assetnum"),11L);
			hang.setValue("starttime",wo.getDate("udvstarttime"),11L);
			hang.setValue("reason", wo.getString("udvreason"),11L);
			hang.setValue("oldstatus", wo.getString("udwostatus"),11L);
			hang.setValue("createby", this.getMbo().getUserInfo().getPersonId(),2L);
//			wo.changeStatus("HANG", MXServer.getMXServer().getDate(), "HANG");
			wo.setValue("UDWOSTATUS", "HANG", 11L);
			this.app.getAppBean().save();
		}else{
			Object[] obj = { "当前状态无法挂起！" };
		    throw new MXApplicationException("udmessage", "error1", obj);
		}
		return super.execute();
	}
}
