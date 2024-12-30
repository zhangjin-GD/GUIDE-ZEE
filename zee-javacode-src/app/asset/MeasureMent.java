package guide.app.asset;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class MeasureMent extends Mbo implements MboRemote {

	public MeasureMent(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			String personId = this.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			String mpointnum = parent.getString("mpointnum");
			String assetnum = parent.getString("assetnum");
			String meternum = parent.getString("meternum");
			this.setValue("mpointnum", mpointnum, 11L);
			this.setValue("assetnum", assetnum, 11L);
			this.setValue("meternum", meternum, 11L);
			this.setValue("measuredate", currentDate, 11L);
			this.setValue("measureby", personId, 11L);
		}
	}
}
