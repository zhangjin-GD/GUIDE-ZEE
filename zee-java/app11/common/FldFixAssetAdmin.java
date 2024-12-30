package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldFixAssetAdmin extends FldComPersonId {

	public FldFixAssetAdmin(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("title like '%固定资产管理员%'");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}
}
