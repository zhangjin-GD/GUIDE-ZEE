package guide.app.pr;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.app.person.FldPersonID;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldPurchaser extends FldPersonID {

	public FldPurchaser(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		String sql = "title like '%采购%' or LOWER(title) like '%purchaser%'";
		String personId = mbo.getUserInfo().getPersonId();
		MboSetRemote mboSet = mbo.getMboSet("$person", "person", "PERSONID='" + personId + "'");
		MboRemote mbo1 = mboSet.getMbo(0);
		String udcompany = mbo1.getString("UDCOMPANY");
		if (udcompany.equals("GR02PCT")) {
			sql = "title like '%采购%' or LOWER(title) like '%purchaser%' and uddept='" + mbo1.getString("uddept") + "'";
		}
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		if (mbo instanceof UDPRLine) {
			Date currentDate = MXServer.getMXServer().getDate();
			if (!this.getMboValue().isNull()) {
				mbo.setValue("udpurchasertime", currentDate, 11L);
			} else {
				mbo.setValueNull("udpurchasertime", 11L);
			}
		}
	}
}
