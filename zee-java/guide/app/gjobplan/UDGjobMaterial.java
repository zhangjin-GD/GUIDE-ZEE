package guide.app.gjobplan;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDGjobMaterial extends Mbo implements MboRemote {

	public UDGjobMaterial(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof UDGjobPlan) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("linenum", linenum, 11L);
			this.setValue("gjpnum", parent.getString("gjpnum"), 11L);
			this.setValue("orderqty", 1, 11L);
		}
	}
}
