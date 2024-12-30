package guide.app.company;

import java.rmi.RemoteException;

import psdi.app.company.Company;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class BankInfo extends Mbo implements MboRemote {
	public BankInfo(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if ((parent != null) && (parent instanceof Company)) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String company = parent.getString("company");
			this.setValue("linenum", linenum, 11L);
			this.setValue("company", company, 11L);
		}
	}
}
