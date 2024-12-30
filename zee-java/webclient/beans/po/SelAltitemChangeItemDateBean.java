package guide.webclient.beans.po;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.common.CommonUtil;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelAltitemChangeItemDateBean extends DataBean {
	
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.getMbo();
		MboRemote owner = mbo.getOwner();
		String personId = owner.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		String tableName = owner.getName();
		long idValue = owner.getUniqueIDValue();
		String oldvalue = owner.getString("itemnum");
		String newvalue = mbo.getString("altitemnum");
		owner.setValue("itemnum", newvalue, 11L);
		String description = CommonUtil.getValue("ITEM", "status='ACTIVE' and itemnum='"+newvalue+"'", "description");
		owner.setValue("description", description, 11L);
		MboSetRemote changeHisSet = owner.getMboSet("UDCHANGEHIS");
		MboRemote changeHis = changeHisSet.add(11L);
		changeHis.setValue("ownerid", idValue, 11L);
		changeHis.setValue("ownertable", tableName, 11L);
		changeHis.setValue("attributename", "ITEMNUM", 11L);
		changeHis.setValue("oldvalue", oldvalue, 11L);
		changeHis.setValue("newvalue", newvalue, 11L);
		changeHis.setValue("reason", "替代件", 11L);
		changeHis.setValue("changeby", personId, 11L);
		changeHis.setValue("changedate", currentDate, 11L);
		return super.execute();
	}
}
