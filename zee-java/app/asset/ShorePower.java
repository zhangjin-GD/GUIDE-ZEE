package guide.app.asset;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class ShorePower extends UDMbo implements MboRemote {

	public ShorePower(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = MXServer.getMXServer().getDate();
		String sysdateStr = sdf.format(currentDate);
		this.setValue("description", sysdateStr + "岸电统计报表");
	}
}
