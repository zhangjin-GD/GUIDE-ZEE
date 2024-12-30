package guide.app.person;

import java.rmi.RemoteException;

import psdi.app.person.FldPersonDelegate;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldPersonDelegate extends FldPersonDelegate {

	public UDFldPersonDelegate(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "udcompany=:udcompany";
		setListCriteria(sql);
		return super.getList();
	}
}
