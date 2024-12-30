package guide.app.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class InvUseSplit extends Mbo implements MboRemote {

	public InvUseSplit(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote owner = this.getOwner();
		if (owner != null) {
			String personId = this.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			String invusenum = owner.getString("invusenum");
			String udwonum = owner.getString("udwonum");
			String assetnum = owner.getString("udwo.assetnum");
			String udprojectnum = owner.getString("udprojectnum");
			String udbudgetnum = owner.getString("udbudgetnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("linenum", linenum, 11L);
			this.setValue("invusenum", invusenum, 11L);
			this.setValue("quantity", 1, 11L);
			this.setValue("wonum", udwonum, 11L);
			this.setValue("assetnum", assetnum, 2L);
			this.setValue("projectnum", udprojectnum, 11L);
			this.setValue("budgetnum", udbudgetnum, 11L);
			this.setValue("createby", personId, 11L);
			this.setValue("createdate", currentDate, 11L);
		}
	}

}
