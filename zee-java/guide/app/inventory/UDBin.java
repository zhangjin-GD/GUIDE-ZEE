package guide.app.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDBin extends Mbo implements MboRemote {

	public UDBin(MboSet ms) throws RemoteException {
		super(ms);
	}

	public void add() throws RemoteException, MXException {
		super.add();
		setValue("sqn", (int)getThisMboSet().max("sqn") +1,11L);
		String personid = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		setValue("createby", personid);
		setValue("createdate",currentDate);
	}
	
}
