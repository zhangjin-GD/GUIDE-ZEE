package guide.app.company;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.app.company.Company;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class CpEvaluate extends Mbo implements MboRemote {

	public CpEvaluate(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if ((parent != null) && (parent instanceof Company)) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String company = parent.getString("company");
			String personid = this.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			this.setValue("linenum", linenum, 11L);
			this.setValue("company", company, 11L);
			this.setValue("createby", personid, 11L);
			this.setValue("createtime", currentDate, 11L);
		}
	}
}
