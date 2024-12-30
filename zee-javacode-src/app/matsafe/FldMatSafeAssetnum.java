package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.app.asset.FldAssetnum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldMatSafeAssetnum extends FldAssetnum {

	public FldMatSafeAssetnum(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String sql = "status in('ACTIVE','OPERATING')";
//		if (!mbo.isNull("udcompany")) {
//			sql += " and udcompany=:udcompany";
//		}
		if (!mbo.isNull("assettypecode")) {
			sql += " and udassettypecode=:assettypecode";
		}
		setListCriteria(sql);
		return super.getList();
	}
}
