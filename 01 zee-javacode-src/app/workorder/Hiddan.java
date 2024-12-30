package guide.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class Hiddan extends Mbo implements MboRemote {

	public Hiddan(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String personid = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		this.setValue("createby", personid, 2L);// 创建人
		this.setValue("createtime", currentDate, 11L);// 创建时间

		MboSetRemote personSet = this.getMboSet("$PERSON", "PERSON", "personid ='" + personid + "'");
		if (personSet != null && !personSet.isEmpty()) {
			MboRemote person = personSet.getMbo(0);
			this.setValue("udcrew", person.getString("udcrew"), 11L);
		}
	}
}
