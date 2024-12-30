package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class ProConLine extends Mbo implements MboRemote {

	public ProConLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();

		try {
			MboSetRemote proPaySet = this.getMboSet("UDPROPAY");
			if (!proPaySet.isEmpty()) {
				this.setFlag(READONLY, true);
			} else {
				this.setFlag(READONLY, false);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof ProCon) {
			String proconnum = parent.getString("proconnum");
			int proconlinenum = (int) getThisMboSet().max("proconlinenum") + 1;
			this.setValue("proconnum", proconnum, 11L);
			this.setValue("proconlinenum", proconlinenum, 11L);
			this.setValue("paycost", 0, 11L);
		}
	}
}
