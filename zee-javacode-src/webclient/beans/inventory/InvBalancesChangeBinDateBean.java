package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class InvBalancesChangeBinDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote parent = this.app.getAppBean().getMbo();
		MboRemote mbo = this.getMbo();
		if (!mbo.isNull("newbinnum") && !mbo.isNull("reason")) {
			String attributename = "UDNEWBINNUM";
			String personId = parent.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			MboSetRemote invBalancesSet = mbo.getMboSet("INVBALANCES");
			MboRemote invBalances = invBalancesSet.getMbo(0);
			String tableName = invBalances.getName();
			long idValue = invBalances.getUniqueIDValue();
			String oldvalue = invBalances.getString(attributename);
			String newvalue = mbo.getString("newbinnum");
			String reason = mbo.getString("reason");

			parent.setValue("binnum", newvalue, 11L);
			invBalances.setValue(attributename, newvalue, 11L);

			MboSetRemote changeHisSet = mbo.getMboSet("UDCHANGEHIS");
			MboRemote changeHis = changeHisSet.add(11L);
			changeHis.setValue("ownerid", idValue, 11L);
			changeHis.setValue("ownertable", tableName, 11L);
			changeHis.setValue("attributename", attributename, 11L);
			changeHis.setValue("oldvalue", oldvalue, 11L);
			changeHis.setValue("newvalue", newvalue, 11L);
			changeHis.setValue("reason", reason, 11L);
			changeHis.setValue("changeby", personId, 11L);
			changeHis.setValue("changedate", currentDate, 11L);
		}
		return super.execute();
	}
}
