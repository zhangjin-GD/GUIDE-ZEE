package guide.app.security;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class SafePlan extends UDMbo implements MboRemote {

	public SafePlan(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String appName = this.getThisMboSet().getApp();

		if (appName != null && !appName.isEmpty()) {
			String appType = appName.replaceAll("UD", ""); // 替换UD
			this.setValue("apptype", appType, 11L);
		}
	}
}
