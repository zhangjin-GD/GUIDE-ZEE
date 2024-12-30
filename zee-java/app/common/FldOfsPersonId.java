package guide.app.common;

import java.rmi.RemoteException;

import psdi.app.person.FldPersonID;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldOfsPersonId extends FldPersonID {

	public FldOfsPersonId(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "udcompany=:udcompany and uddept=:uddept and udofs=:udofs";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}
	
}
