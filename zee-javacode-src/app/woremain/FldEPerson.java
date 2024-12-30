package guide.app.woremain;

import guide.app.common.FldOfsPersonId;

import java.rmi.RemoteException;

import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldEPerson extends FldOfsPersonId {

	public FldEPerson(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "title like '%主管%'";
		MboSetRemote mboSetRemote = super.getList();
		mboSetRemote.setWhere(sql);
		return mboSetRemote;
	}

}
