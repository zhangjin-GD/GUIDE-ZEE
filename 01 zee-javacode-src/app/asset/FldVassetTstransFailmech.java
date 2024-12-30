package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldVassetTstransFailmech extends MAXTableDomain {

	public FldVassetTstransFailmech(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDFAILCLASS", "failclassnum=:" + thisAttr);
		String[] FromStr = { "failclassnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "1=2";
		Mbo mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String assetnum = "";
			String appName = owner.getThisMboSet().getApp();
			if ("UDASSETTS".equalsIgnoreCase(appName) || "UDTRAILER".equalsIgnoreCase(appName)) {
				assetnum = mbo.getString("eqnum");
			} else if ("UDASSET".equalsIgnoreCase(appName) || "UDWOEM".equalsIgnoreCase(appName)
					|| "UDWOCM".equalsIgnoreCase(appName)) {
				assetnum = owner.getString("assetnum");
			}
			MboSetRemote assetSet = mbo.getMboSet("$ASSET", "ASSET", "assetnum='" + assetnum + "'");
			if (!assetSet.isEmpty() && assetSet.count() > 0) {
				MboRemote asset = assetSet.getMbo(0);
				String udassettypecode = asset.getString("udassettypecode");
				sql = "type='" + udassettypecode + "' and classtype='A'";
			}
		}
		setListCriteria(sql);
		return super.getList();
	}
}
