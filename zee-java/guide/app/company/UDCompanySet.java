package guide.app.company;

import java.rmi.RemoteException;

import psdi.app.company.CompanySet;
import psdi.app.company.CompanySetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDCompanySet extends CompanySet implements CompanySetRemote {

	public UDCompanySet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDCompany(ms);
	}
	
	
}
