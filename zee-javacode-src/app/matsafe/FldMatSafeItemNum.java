package guide.app.matsafe;

import java.rmi.RemoteException;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldMatSafeItemNum extends MAXTableDomain {
	public FldMatSafeItemNum(MboValue mbv) throws MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ITEM", "ITEMNUM=:" + thisAttr);
		String[] FromStr = { "ITEMNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("udisfix=0 and udissafety=1 and status in ('ACTIVE')");
		return super.getList();
	}

	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		// 主表标准值
		MboSetRemote itemSet = mbo.getMboSet("ITEM");
		if ((!itemSet.isEmpty()) && (itemSet.count() > 0)) {
			MboRemote item = itemSet.getMbo(0);
			if (!item.isNull("udlockstd")) {
				double lockstd = item.getDouble("udlockstd");
				mbo.setValue("lockstd", lockstd, 11L);
			}
			if (!item.isNull("udteustd")) {
				double teustd = item.getDouble("udteustd");
				mbo.setValue("teustd", teustd, 11L);
			}
			if (!item.isNull("udunitstd")) {
				double unitstd = item.getDouble("udunitstd");
				mbo.setValue("unitstd", unitstd, 11L);
			}
			if (!item.isNull("udactionstd")) {
				double actionstd = item.getDouble("udactionstd");
				mbo.setValue("actionstd", actionstd, 11L);
			}
			if (!item.isNull("udrunstd")) {
				double runstd = item.getDouble("udrunstd");
				mbo.setValue("runstd", runstd, 11L);
			}
			if (!item.isNull("udcalstd")) {
				double calstd = item.getDouble("udcalstd");
				mbo.setValue("calstd", calstd, 11L);
			}
		}
		// 码头本身标准值
		MboSetRemote itemCPSet = mbo.getMboSet("UDITEMCP");
		if ((!itemCPSet.isEmpty()) && (itemCPSet.count() > 0)) {
			MboRemote itemCP = itemCPSet.getMbo(0);
			if (!itemCP.isNull("lockstd")) {
				double lockstd = itemCP.getDouble("lockstd");
				mbo.setValue("lockstd", lockstd, 11L);
			}
			if (!itemCP.isNull("teustd")) {
				double teustd = itemCP.getDouble("teustd");
				mbo.setValue("teustd", teustd, 11L);
			}
			if (!itemCP.isNull("unitstd")) {
				double unitstd = itemCP.getDouble("unitstd");
				mbo.setValue("unitstd", unitstd, 11L);
			}
			if (!itemCP.isNull("actionstd")) {
				double actionstd = itemCP.getDouble("actionstd");
				mbo.setValue("actionstd", actionstd, 11L);
			}
			if (!itemCP.isNull("runstd")) {
				double runstd = itemCP.getDouble("runstd");
				mbo.setValue("runstd", runstd, 11L);
			}
			if (!itemCP.isNull("calstd")) {
				double calstd = itemCP.getDouble("calstd");
				mbo.setValue("calstd", calstd, 11L);
			}
		}
		String assetnum = mbo.getString("assetnum");// 设备
		String matsafetype = mbo.getString("matsafetype");// 安全件类型
		String part = mbo.getString("part");// 机构
		String description = mbo.getString("description");// 安全件描述
		String itemnum = mbo.getString("itemnum");
		// 找到父级
		String sql = "status = 'RUN' and assetnum = '" + assetnum + "' and matsafetype = '" + matsafetype
				+ "' and description = '" + description + "' and itemnum ='" + itemnum + "'";
		if (!mbo.isNull("part")) {
			sql += " and part = '" + part + "'";
		}
		// 上一个（旧）安全件
		MboSetRemote matsafeSet = mbo.getMboSet("$UDMATSAFE", "UDMATSAFE", sql);
		if (!matsafeSet.isEmpty() && matsafeSet.count() > 0) {
			MboRemote matsafe = matsafeSet.getMbo(0);
			if (!matsafe.isNull("lockstd")) {
				double lockstd = matsafe.getDouble("lockstd");
				mbo.setValue("lockstd", lockstd, 11L);
			}
			if (!matsafe.isNull("teustd")) {
				double teustd = matsafe.getDouble("teustd");
				mbo.setValue("teustd", teustd, 11L);
			}
			if (!matsafe.isNull("unitstd")) {
				double unitstd = matsafe.getDouble("unitstd");
				mbo.setValue("unitstd", unitstd, 11L);
			}
			if (!matsafe.isNull("actionstd")) {
				double actionstd = matsafe.getDouble("actionstd");
				mbo.setValue("actionstd", actionstd, 11L);
			}
			if (!matsafe.isNull("runstd")) {
				double runstd = matsafe.getDouble("runstd");
				mbo.setValue("runstd", runstd, 11L);
			}
			if (!matsafe.isNull("calstd")) {
				double calstd = matsafe.getDouble("calstd");
				mbo.setValue("calstd", calstd, 11L);
			}
		}
	}
}