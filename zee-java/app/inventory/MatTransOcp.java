package guide.app.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class MatTransOcp extends Mbo implements MboRemote {

	public MatTransOcp(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			String personId = this.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			String location = parent.getString("location");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("location", location, 11L);
			this.setValue("linenum", linenum, 11L);
			this.setValue("totalprice", 0, 11L);
			this.setValue("totalcost", 0, 11L);
			this.setValue("createby", personId, 11L);
			this.setValue("createtime", currentDate, 11L);
		}
	}
}
