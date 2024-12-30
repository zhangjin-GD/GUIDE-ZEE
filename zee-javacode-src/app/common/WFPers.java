package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class WFPers extends Mbo implements MboRemote {

	public WFPers(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String name = parent.getName();
			long ownerid = parent.getUniqueIDValue();
			String appName = parent.getThisMboSet().getApp();
			String appType = appName.replaceAll("UD", ""); // 替换UD
			this.setValue("linenum", linenum, 11L);
			this.setValue("ownertable", name, 11L);
			this.setValue("ownerid", ownerid, 11L);
			this.setValue("apptype", appType, 11L);
		}
	}
}
