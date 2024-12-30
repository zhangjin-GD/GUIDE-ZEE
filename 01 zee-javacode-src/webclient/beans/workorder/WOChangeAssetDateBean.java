package guide.webclient.beans.workorder;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class WOChangeAssetDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote parent = this.app.getAppBean().getMbo();
		MboRemote mbo = this.getMbo();
		if (!mbo.isNull("newassetnum")) {
			String attributename = "ASSETNUM";
			String personId = parent.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			MboSetRemote woSet = mbo.getMboSet("workorder");
			MboRemote wo = woSet.getMbo(0);
			String tableName = wo.getName();
			long idValue = wo.getUniqueIDValue();
			String oldvalue = wo.getString(attributename);
			String newvalue = mbo.getString("NEWASSETNUM");
			String udassettypecode = mbo.getString("newassetnum.udassettypecode");
			parent.setValue("udassettypecode", udassettypecode, 11L);
			parent.setValue(attributename, newvalue, 11L);

			MboSetRemote changeHisSet = mbo.getMboSet("UDCHANGEHIS");
			MboRemote changeHis = changeHisSet.add(11L);
			changeHis.setValue("ownerid", idValue, 11L);
			changeHis.setValue("ownertable", tableName, 11L);
			changeHis.setValue("attributename", attributename, 11L);
			changeHis.setValue("oldvalue", oldvalue, 11L);
			changeHis.setValue("newvalue", newvalue, 11L);
			changeHis.setValue("reason", "设备变更", 11L);
			changeHis.setValue("changeby", personId, 11L);
			changeHis.setValue("changedate", currentDate, 11L);
		}
		app.getAppBean().save();
		return 1;
	}
}
