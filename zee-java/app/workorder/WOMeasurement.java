package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class WOMeasurement extends Mbo implements MboRemote {

	public WOMeasurement(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof UDWO) {
			String wonum = parent.getString("wonum");
			String assetnum = parent.getString("assetnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("wonum", wonum, 11L);
			this.setValue("assetnum", assetnum, 11L);
			this.setValue("linenum", linenum, 11L);
			this.setValue("createby", getUserInfo().getPersonId(), 2L);// 创建人
			this.setValue("createtime", MXServer.getMXServer().getDate(), 11L);// 创建时间
		}
	}

}
