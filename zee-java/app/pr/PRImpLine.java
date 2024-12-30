package guide.app.pr;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashSet;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValueInfo;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class PRImpLine extends Mbo implements MboRemote {

	private static HashSet<String> skipFieldCopy = new HashSet<String>();

	private static boolean isHashSetLoaded = false;

	public PRImpLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof PRImp) {
			String primpnum = parent.getString("primpnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String personid = this.getUserInfo().getPersonId();
			Date sysdate = MXServer.getMXServer().getDate();
			this.setValue("primpnum", primpnum, 11L);
			this.setValue("linenum", linenum, 11L);
			this.setValue("orderqty", 1, 11L);
			this.setValue("unitcost", 0.0D, 2L);
			this.setValue("enterby", personid, 11L);
			this.setValue("enterdate", sysdate, 11L);
			this.setValue("status", "APPR", 11L);
			MboSetRemote maxUserSet = this.getMboSet("$MAXUSER", "MAXUSER");
			maxUserSet.setWhere("personid ='" + personid + "'");
			maxUserSet.reset();
			if (maxUserSet != null && !maxUserSet.isEmpty()) {
				MboRemote maxUser = maxUserSet.getMbo(0);
				this.setValue("storeloc", maxUser.getString("defstoreroom"), 2L);
			}
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if (!this.toBeDeleted() && this.getDouble("unitcost") <= 0) {
			throw new MXApplicationException("guide", "1035");
		}
	}

	@Override
	protected boolean skipCopyField(MboValueInfo mvi) {

		if (!isHashSetLoaded) {
			loadSkipFieldCopyHashSet();
		}
		return skipFieldCopy.contains(mvi.getName());
	}

	private void loadSkipFieldCopyHashSet() {

		isHashSetLoaded = true;
		skipFieldCopy.add("PRIMPNUM");
		skipFieldCopy.add("ENTERDATE");
	}
}
