package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldAssetTstransEqnum extends MAXTableDomain {

	public FldAssetTstransEqnum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ASSET", "ASSETNUM=:" + thisAttr);
		String[] FromStr = { "ASSETNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		Mbo mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String sql = "1=1";
		if (owner != null) {
			String appName = owner.getThisMboSet().getApp();
			String assetnum = owner.getString("assetnum");
			String type = mbo.getString("type");
			// 吊具
			if ("UDASSETTS".equalsIgnoreCase(appName)) {
				sql += " and udassettypecode in ('QC','RTG','RMG','RS','MHC','FL') and udeqnum is null";
			}
			// 设备设施/抢修工单/状态维修工单
			else if ("UDASSET".equalsIgnoreCase(appName) || "UDWOEM".equalsIgnoreCase(appName)
					|| "UDWOCM".equalsIgnoreCase(appName)) {
				if ("UPPER".equalsIgnoreCase(type)) {
					sql += " and udassettypecode in('SQ','SY','SR','SM','SOF','SE','LH','LB','GB','FB','TL') and udeqnum is null";
				} else if ("LOWER".equalsIgnoreCase(type)) {
					sql += " and udeqnum='" + assetnum + "'";
				}
			}
			// 拖架
			else if ("UDTRAILER".equalsIgnoreCase(appName)) {
				if ("UPPER".equalsIgnoreCase(type)) {
					sql += " and udassettypecode in('TT') and udeqnum is null";
				} else if ("LOWER".equalsIgnoreCase(type)) {
					sql += " and udeqnum='" + assetnum + "'";
				}
			}
			// 其它
			else {
				if ("UPPER".equalsIgnoreCase(type)) {
					sql += " and udeqnum is null";
				} else if ("LOWER".equalsIgnoreCase(type)) {
					sql += " and udeqnum='" + assetnum + "'";
				}
			}
		}
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String appName = owner.getThisMboSet().getApp();
			if ("UDASSET".equalsIgnoreCase(appName) || "UDWOEM".equalsIgnoreCase(appName)
					|| "UDWOCM".equalsIgnoreCase(appName)) {
				MboSetRemote assetSet = mbo.getMboSet("ASSET");
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					MboRemote asset = assetSet.getMbo(0);
					double workhour = asset.getDouble("udworkhour");
					double boxunit = asset.getDouble("udboxunit");
					double boxteu = asset.getDouble("udboxteu");
					double electrickwh = asset.getDouble("udelectrickwh");
					double oill = asset.getDouble("udoill");

					mbo.setValue("workhour", workhour, 11L);
					mbo.setValue("boxunit", boxunit, 11L);
					mbo.setValue("boxteu", boxteu, 11L);
					mbo.setValue("electrickwh", electrickwh, 11L);
					mbo.setValue("oill", oill, 11L);
				} else {
					mbo.setValue("workhour", 0, 11L);
					mbo.setValue("boxunit", 0, 11L);
					mbo.setValue("boxteu", 0, 11L);
					mbo.setValue("electrickwh", 0, 11L);
					mbo.setValue("oill", 0, 11L);
				}
			}
		}
	}
}
