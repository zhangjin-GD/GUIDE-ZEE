package guide.app.po;

import java.rmi.RemoteException;

import psdi.app.person.FldPersonID;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPoAuthorizer extends FldPersonID {

	public FldPoAuthorizer(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "udcompany=:udcompany and udhrusernum is not null";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}
	
}
