package guide.app.matrecord;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class Matrecord extends Mbo implements MboRemote {

	public Matrecord(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		Date currentDate = MXServer.getMXServer().getDate();
		MboRemote owner = this.getOwner();
		if (owner != null) {
			String wonum = owner.getString("wonum");
			this.setValue("wonum", wonum,11L);
		}
		this.setValue("RECORDDATE", currentDate,11L);
	}
}
