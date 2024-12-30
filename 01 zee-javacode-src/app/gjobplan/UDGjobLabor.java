package guide.app.gjobplan;

import java.rmi.RemoteException;

import guide.app.gpm.UDGpm;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDGjobLabor extends Mbo implements MboRemote {

	public UDGjobLabor(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			if (parent instanceof UDGjobPlan) {
				this.setValue("gjpnum", parent.getString("gjpnum"), 11L);
			}
			if (parent instanceof UDGpm) {
				this.setValue("gpmnum", parent.getString("gpmnum"), 11L);
			}
		}
	}
}
