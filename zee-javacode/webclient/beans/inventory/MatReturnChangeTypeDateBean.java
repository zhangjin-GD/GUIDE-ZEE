package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class MatReturnChangeTypeDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote parent = this.app.getAppBean().getMbo();
		MboRemote mbo = this.getMbo();
		if (!mbo.isNull("reason")) {
			String personId = parent.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			MboSetRemote relationshipSet = mbo.getMboSet("UDMATRETURN");
			if (!mbo.isNull("returntype")) {
				String attributename = "RETURNTYPE";
				MboRemote relationship = relationshipSet.getMbo(0);
				String tableName = relationship.getName();
				long idValue = relationship.getUniqueIDValue();
				String oldvaluedesc = relationship.getString("returntype.description");
				String newvalue = mbo.getString(attributename);
				String newvaluedesc = mbo.getString("returntype.description");
				String reason = mbo.getString("reason");
				relationship.setValue(attributename, newvalue, 11L);
				MboSetRemote changeHisSet = mbo.getMboSet("UDCHANGEHIS");
				MboRemote changeHis = changeHisSet.add(11L);
				changeHis.setValue("ownerid", idValue, 11L);
				changeHis.setValue("ownertable", tableName, 11L);
				changeHis.setValue("attributename", attributename, 11L);
				changeHis.setValue("oldvalue", oldvaluedesc, 11L);
				changeHis.setValue("newvalue", newvaluedesc, 11L);
				changeHis.setValue("reason", reason, 11L);
				changeHis.setValue("changeby", personId, 11L);
				changeHis.setValue("changedate", currentDate, 11L);
			}
			if (!mbo.isNull("returntype1")) {
				String attributename = "RETURNTYPE1";
				MboRemote relationship = relationshipSet.getMbo(0);
				String tableName = relationship.getName();
				long idValue = relationship.getUniqueIDValue();
				String oldvaluedesc = relationship.getString("returntype1.description");
				String newvalue = mbo.getString(attributename);
				String newvaluedesc = mbo.getString("returntype1.description");
				String reason = mbo.getString("reason");
				relationship.setValue(attributename, newvalue, 11L);
				MboSetRemote changeHisSet = mbo.getMboSet("UDCHANGEHIS");
				MboRemote changeHis = changeHisSet.add(11L);
				changeHis.setValue("ownerid", idValue, 11L);
				changeHis.setValue("ownertable", tableName, 11L);
				changeHis.setValue("attributename", attributename, 11L);
				changeHis.setValue("oldvalue", oldvaluedesc, 11L);
				changeHis.setValue("newvalue", newvaluedesc, 11L);
				changeHis.setValue("reason", reason, 11L);
				changeHis.setValue("changeby", personId, 11L);
				changeHis.setValue("changedate", currentDate, 11L);
			}
			if (!mbo.isNull("location")) {
				String attributename = "LOCATION";
				MboRemote relationship = relationshipSet.getMbo(0);
				String tableName = relationship.getName();
				long idValue = relationship.getUniqueIDValue();
				String oldvalue = relationship.getString("location");
				String newvalue = mbo.getString(attributename);
				String reason = mbo.getString("reason");
				relationship.setValue(attributename, newvalue, 11L);
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
		}
		return super.execute();
	}
}
