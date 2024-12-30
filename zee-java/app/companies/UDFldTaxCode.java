package guide.app.companies;

import psdi.app.financial.FldTaxCode;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

import java.rmi.RemoteException;

public class UDFldTaxCode extends FldTaxCode {
	public UDFldTaxCode(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote list = super.getList();
		list.setWhere(
				"taxcode in ('1A','1B','1C','1D','1E','1G','2A','2B','2C','2D','2E','2F','2G','2H','4A','4C','4D','4E','4F')");
		return list;
	}
}
