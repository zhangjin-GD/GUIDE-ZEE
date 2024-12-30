package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.rfq.RFQSet;
import psdi.app.rfq.RFQSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDRFQSet extends RFQSet implements RFQSetRemote{

	public UDRFQSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet var1) throws MXException, RemoteException {
		return new UDRFQ(var1);
	}
}
