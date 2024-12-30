package guide.app.project;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class ProCon extends UDMbo implements MboRemote {

	public ProCon(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			String status = this.getString("status");
			if ("CAN".equals(status) || "REVISD".equals(status) || "APPR".equals(status)) {
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
		this.setValue("totalcost", 0, 11L);
		this.setValue("tax", 0, 11L);
		this.setValue("pretaxcost", 0, 11L);
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
	}
}
