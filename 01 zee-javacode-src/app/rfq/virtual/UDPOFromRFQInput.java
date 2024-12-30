package guide.app.rfq.virtual;

import java.rmi.RemoteException;

import psdi.app.rfq.virtual.POFromRFQInput;
import psdi.app.rfq.virtual.POFromRFQInputRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDPOFromRFQInput extends POFromRFQInput implements POFromRFQInputRemote{

	public UDPOFromRFQInput(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

}
