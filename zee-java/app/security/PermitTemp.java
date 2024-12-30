package guide.app.security;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class PermitTemp extends Mbo implements MboRemote {

	public PermitTemp(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String appName = this.getThisMboSet().getApp();
		if (appName != null && !appName.isEmpty()) {
			if ("UDPERMITTA".equalsIgnoreCase(appName)) {// 安全措施
				this.setValue("temptype", "REMEDY", 11L);
			} else if ("UDPERMITTB".equalsIgnoreCase(appName)) {// 分析数据
				this.setValue("temptype", "ANALYSIS", 11L);
			}
		}
	}
}
