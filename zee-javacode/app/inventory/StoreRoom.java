package guide.app.inventory;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class StoreRoom extends UDMbo implements MboRemote{

	public StoreRoom(MboSet ms) throws RemoteException {
		super(ms);
	}
	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String personid = this.getUserInfo().getPersonId();
		MboSetRemote maxUserSet = this.getMboSet("$MAXUSER", "MAXUSER");
		maxUserSet.setWhere("personid ='" + personid + "'");
		maxUserSet.reset();
		if (maxUserSet != null && !maxUserSet.isEmpty()) {
			MboRemote maxUser = maxUserSet.getMbo(0);
			this.setValue("location", maxUser.getString("defstoreroom"), 11L);
		}
	}
}
