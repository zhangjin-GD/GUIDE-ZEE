package guide.webclient.beans.rfq;

import java.rmi.RemoteException;

import guide.app.rfq.UDRFQ;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.rfq.RFQAppBean;

public class UDRFQAppBean extends RFQAppBean {

	@Override
	public int SENT() throws MXException, RemoteException {
		UDRFQ mbo = (UDRFQ) this.getMbo();
		MboSetRemote rfqVendorSet = mbo.getMboSet("RFQVENDOR");
		if (rfqVendorSet == null || rfqVendorSet.isEmpty()) {
			throw new MXApplicationException("guide", "1120");
		}
		return super.SENT();
	}

}
