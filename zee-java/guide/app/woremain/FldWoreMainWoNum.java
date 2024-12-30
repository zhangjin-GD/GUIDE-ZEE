package guide.app.woremain;

import java.rmi.RemoteException;

import psdi.app.workorder.FldWonum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldWoreMainWoNum extends FldWonum {

	public FldWoreMainWoNum(MboValue mbv) throws MXException {
		super(mbv);

	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "status not in ('CAN','CLOSE')";
		setListCriteria(sql);
		return super.getList();
	}

	public String[] getAppLink() throws MXException, RemoteException {

		MboRemote mbo = this.getMboValue().getMbo();
		String thisAttr = getMboValue().getAttributeName();
		String wonum = mbo.getString(thisAttr);
		String appType = getWorktype(wonum);
		if (appType == null || appType.equalsIgnoreCase("")) {
			return new String[] { "" };
		} else if (appType.equalsIgnoreCase("PM") || appType.equalsIgnoreCase("IM")) {
			return new String[] { "UDWOPM" };
		} else if (appType.equalsIgnoreCase("CM")) {
			return new String[] { "UDWOCM" };
		} else if (appType.equalsIgnoreCase("EM")) {
			return new String[] { "UDWOEM" };
		} else if (appType.equalsIgnoreCase("FM")) {
			return new String[] { "UDWOFM" };
		} else {
			return super.getAppLink();
		}

	}

	private String getWorktype(String wonum) throws RemoteException, MXException {
		MboSetRemote woSet = MXServer.getMXServer().getMboSet("WORKORDER", MXServer.getMXServer().getSystemUserInfo());
		woSet.setWhere("wonum = '" + wonum + "'");
		if (!woSet.isEmpty() && woSet.count() > 0) {
			String typeValue = woSet.getMbo(0).getString("worktype");
			woSet.close();
			return typeValue;
		}
		woSet.close();
		return "";
	}
}
