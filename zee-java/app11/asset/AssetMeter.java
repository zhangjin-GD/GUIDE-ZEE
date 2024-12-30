package guide.app.asset;11111111

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class AssetMeter extends Mbo implements MboRemote {

	public AssetMeter(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			String personId = this.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			String assetnum = parent.getString("assetnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("linenum", linenum, 11L);
			this.setValue("assetnum", assetnum, 11L);
			this.setValue("createby", personId, 11L);// 创建人
			this.setValue("createtime", currentDate, 11L);// 创建时间
			this.setValue("value", 0, 11L);
		}
	}
}
