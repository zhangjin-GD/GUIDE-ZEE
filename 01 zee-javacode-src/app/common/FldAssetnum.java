package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldAssetnum extends MAXTableDomain {

	
	public FldAssetnum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ASSET", "assetnum =:" + thisAttr);
		String[] FromStr = { "assetnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "status in('ACTIVE','OPERATING')";
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
		}
		if (getMboValue().isNull()) {
			mbo.setValueNull("udcompany", 11L);
			mbo.setValueNull("uddept", 11L);
			mbo.setValueNull("udofs", 11L);
		}
	}
	
}
