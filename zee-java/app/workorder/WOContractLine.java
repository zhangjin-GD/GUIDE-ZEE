package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class WOContractLine extends Mbo implements MboRemote {

	public WOContractLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			int workorderid = parent.getInt("workorderid");
			String wonum = parent.getString("wonum");
			String appName = parent.getThisMboSet().getApp();
			String appType = appName.replaceAll("UD", ""); // 替换UD
			this.setValue("linenum", linenum, 11L);
			this.setValue("workorderid", workorderid, 11L);
			this.setValue("wonum", wonum, 11L);
			this.setValue("apptype", appType, 11L);
		}
	}
}
