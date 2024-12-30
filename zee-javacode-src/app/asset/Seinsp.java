package guide.app.asset;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.app.asset.Asset;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class Seinsp extends Mbo implements MboRemote {

	public Seinsp(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof Asset) {
			String personid = this.getUserInfo().getPersonId();
			Date sysdate = MXServer.getMXServer().getDate();
			String assetnum = parent.getString("assetnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("linenum", linenum, 11L);
			this.setValue("assetnum", assetnum, 11L);
			this.setValue("createby", personid, 11L);
			this.setValue("createtime", sysdate, 11L);
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof Asset) {
			if (!this.toBeDeleted()) {
				Date checkdate = this.getDate("checkdate");
				Date nextcheckdate = this.getDate("nextcheckdate");
				parent.setValue("checkdate", checkdate, 11L);
				parent.setValue("nextcheckdate", nextcheckdate, 11L);
			}
		}
	}
}
