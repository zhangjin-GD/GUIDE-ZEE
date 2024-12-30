package guide.app.workorder;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class WOSettle extends UDMbo implements MboRemote {

	public WOSettle(MboSet ms) throws RemoteException {
		super(ms);
	}
	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String personId = this.getUserInfo().getPersonId();
		this.setValue("leadby", personId, 2L);
		this.setValue("totalcost", 0, 11L);
	}
}
