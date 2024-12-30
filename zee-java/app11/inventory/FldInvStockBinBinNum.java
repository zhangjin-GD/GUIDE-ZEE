package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldInvStockBinBinNum extends MAXTableDomain {

	public FldInvStockBinBinNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("INVBALANCES", "binnum=:" + thisAttr);
		String[] FromStr = { "binnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String udcompany = owner.getString("UDCOMPANY");
			String sql = "";
			MboSetRemote mboLocSet = owner.getMboSet("UDINVSTOCKLOC");
			if (!mboLocSet.isEmpty() && mboLocSet.count() > 0) {
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < mboLocSet.count(); i++) {
					MboRemote mboloc = mboLocSet.getMbo(i);
					buf.append("'").append(mboloc.getString("storeloc")).append("',");
				}
				sql = "invbalancesid in (select max(invbalancesid) from invbalances where location in ("+ buf.substring(0, buf.length() - 1) + ") group by binnum)";
			} else {
				sql = "invbalancesid in (select max(invbalancesid) from invbalances where location in (select location from locations where udcompany='"+ udcompany + "') group by binnum)";
			}
			setListCriteria(sql);
		}

		return super.getList();
	}
}
