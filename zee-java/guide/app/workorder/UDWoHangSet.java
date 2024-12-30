package guide.app.workorder;

import java.rmi.RemoteException;

import guide.app.common.UDMboSet;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDWoHangSet extends UDMboSet {
	
	public UDWoHangSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDWoHang(ms);
	}
	
}
