package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldAssetNum extends MAXTableDomain {

	public FldAssetNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ASSET", "assetnum =:" + thisAttr);
		String[] FromStr = { "assetnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "status in('ACTIVE','OPERATING') and udcompany=:udcompany and uddept=:uddept and udassettypecode=(select assettype from udgjobplan where gjpnum=:gjpnum)";
		/**
		 * ZEE
		 * 2023-07-20 16:06:08
		 */
		MboRemote mbo = getMboValue().getMbo();
		String udcompany = mbo.getString("udcompany");
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			sql = " status='ACTIVE' and udcompany='"+udcompany+"' ";
		}
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote assetSet = mbo.getMboSet("ASSET");
		if (!assetSet.isEmpty() && assetSet.count() > 0) {
			MboRemote asset = assetSet.getMbo(0);
			mbo.setValue("udcompany", asset.getString("udcompany"), 11L);
			mbo.setValue("uddept", asset.getString("uddept"), 11L);
			mbo.setValue("udofs", asset.getString("udofs"), 11L);
//			mbo.setValue("udcrew", asset.getString("udcrew"), 11L);
		}
	}

}
