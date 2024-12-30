package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.rfq.RFQVendorSet;
import psdi.app.rfq.RFQVendorSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDRFQVendorSet extends RFQVendorSet implements RFQVendorSetRemote{

	public UDRFQVendorSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	
	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDRFQVendor(ms);
	}
}
