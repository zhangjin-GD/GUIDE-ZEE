package guide.app.asset;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class EqRunLogLine extends Mbo implements MboRemote {

	public EqRunLogLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof EqRunLog) {
			String eqrunnum = parent.getString("eqrunnum");
			Date startdate = parent.getDate("startdate");
			Date enddate = parent.getDate("enddate");
			this.setValue("eqrunnum", eqrunnum, 11L);
			this.setValue("startdate", startdate, 11L);
			this.setValue("enddate", enddate, 11L);
		}
	}
}
