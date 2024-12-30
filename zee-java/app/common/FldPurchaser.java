package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPurchaser extends FldComPersonId {

	public FldPurchaser(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("title like '%采购员%' or LOWER(title) like '%purchaser%'");
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}
}
