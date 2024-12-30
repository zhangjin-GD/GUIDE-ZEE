package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class ProConReLine extends Mbo implements MboRemote {

	public ProConReLine(MboSet ms) throws RemoteException {
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
		if (parent != null && parent instanceof ProConRe) {

			String proconnum = parent.getString("proconnum");
			String proconrenum = parent.getString("proconrenum");
			int proconlinenum = (int) getThisMboSet().max("proconlinenum") + 1;

			this.setValue("proconnum", proconnum, 11L);
			this.setValue("proconrenum", proconrenum, 11L);
			this.setValue("proconlinenum", proconlinenum, 11L);
			this.setValue("paycost", 0, 2L);
		}
	}

}
