package guide.app.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class ReceiptLine extends Mbo implements MboRemote {

	public ReceiptLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			long ownerid = parent.getUniqueIDValue();
			String ownertable = parent.getName();
			String personId = this.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			String itemnum = parent.getString("itemnum");
			String description = parent.getString("description");
			String remark = parent.getString("remark");
			this.setValue("ownerid", ownerid, 11L);
			this.setValue("ownertable", ownertable, 11L);
			this.setValue("createby", personId, 11L);
			this.setValue("createtime", currentDate, 11L);
			this.setValue("itemnum", itemnum, 11L);
			this.setValue("description", description, 11L);
			this.setValue("poremark", remark, 11L);
		}
	}
}
