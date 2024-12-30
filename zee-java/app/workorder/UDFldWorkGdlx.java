package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldWorkGdlx extends MAXTableDomain {

	public UDFldWorkGdlx(MboValue mbv) throws MXException {
		super(mbv);
		String attrname = getMboValue().getName();
		setRelationship("ALNDOMAIN", "value = :" + attrname);
		setKeyMap("ALNDOMAIN", new String[] { attrname }, new String[] { "value" });
	}

	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		String sql = " domainid='UDGDLX'";
		String appName = mbo.getThisMboSet().getApp();
		if (appName != null && appName.equalsIgnoreCase("UDWOZEE")) {
			String userInfo = mbo.getUserInfo().getPersonId();
			MboSetRemote persongroupSet = MXServer.getMXServer().getMboSet("PERSONGROUPTEAM",
					MXServer.getMXServer().getSystemUserInfo());
			persongroupSet.setWhere("respparty='" + userInfo
					+ "' and persongroup=(select persongroup from persongroup where description like '管理人员%')");
			persongroupSet.reset();
			if (persongroupSet.isEmpty() && persongroupSet.count() == 0) {
				sql += " and value in ('A','B') ";
			}
			persongroupSet.close();
		}
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String appName = mbo.getThisMboSet().getApp();
		if (appName != null && appName.equalsIgnoreCase("UDWOZEE")) {
			if (mbo.isModified("udgdlx")) {
				mbo.setValueNull("worktype");
			}
		}

	}
}
