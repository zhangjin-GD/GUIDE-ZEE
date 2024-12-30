package guide.app.matsafe;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class MatSafeInsp extends Mbo implements MboRemote {

	public MatSafeInsp(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof MatSafe) {
			String personId = this.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			String matsafenum = parent.getString("matsafenum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("matsafenum", matsafenum, 11L);
			this.setValue("createby", personId, 11L);
			this.setValue("createtime", currentDate, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
