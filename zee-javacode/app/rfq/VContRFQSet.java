package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.NonPersistentMboSet;
import psdi.mbo.NonPersistentMboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class VContRFQSet extends NonPersistentMboSet implements NonPersistentMboSetRemote {

	public VContRFQSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new VContRFQ(ms);
	}

	public void execute() throws MXException, RemoteException {
		if (this.getMbo(0) != null) {
			if (this.getMbo(0).isNull("description")) {
				throw new MXApplicationException("guide", "1110");
			}

			if (this.getMbo(0).isNull("startdate")) {
				throw new MXApplicationException("guide", "1111");
			}
			if (this.getMbo(0).isNull("enddate")) {
				throw new MXApplicationException("guide", "1112");
			}
			if (this.getMbo(0).isNull("limitmaxcost") || this.getMbo(0).getDouble("limitmaxcost") < 0) {
				throw new MXApplicationException("guide", "1113");
			}
			UDRFQVendor rfqVendor = (UDRFQVendor) this.getOwner();
			rfqVendor.createConFromRFQ(this.getMbo(0));
		}
	}
}
