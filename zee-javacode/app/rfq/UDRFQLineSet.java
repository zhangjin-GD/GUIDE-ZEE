package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.rfq.RFQLineSet;
import psdi.app.rfq.RFQLineSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDRFQLineSet extends RFQLineSet implements RFQLineSetRemote {

	public UDRFQLineSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet var1) throws MXException, RemoteException {
		return new UDRFQLine(var1);
	}
	
}
