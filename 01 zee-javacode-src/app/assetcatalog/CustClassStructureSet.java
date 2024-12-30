package guide.app.assetcatalog;

import java.rmi.RemoteException;

import psdi.app.assetcatalog.ClassStructureSet;
import psdi.app.assetcatalog.ClassStructureSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class CustClassStructureSet extends ClassStructureSet implements ClassStructureSetRemote {

	public CustClassStructureSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new CustClassStructure(ms);
	}
}
