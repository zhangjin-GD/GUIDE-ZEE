package guide.app.companies;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

import java.rmi.RemoteException;

import guide.app.common.UDMboSet;

public class UDCompaniesSet extends UDMboSet implements MboSetRemote {

	public UDCompaniesSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet mboSet) throws MXException, RemoteException {
		return new UDCompanies(mboSet);
	}
}
