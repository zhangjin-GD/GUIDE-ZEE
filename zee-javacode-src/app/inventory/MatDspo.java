package guide.app.inventory;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class MatDspo extends UDMbo implements MboRemote {

	public MatDspo(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		this.setValue("linecost", 0, 11L);
		this.setValue("linecost1", 0, 11L);
		this.setValue("linecost2", 0, 11L);
	}
}
