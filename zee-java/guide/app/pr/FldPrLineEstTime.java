package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPrLineEstTime extends MAXTableDomain {

	public FldPrLineEstTime(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("alndomain", "value=:" + thisAttr);
		String[] FromStr = { "value" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "1=2";
		MboRemote mbo = getMboValue().getMbo();
		if (!mbo.isNull("itemnum")) {
			sql = "domainid = 'UDESTTIME'";
			MboSetRemote itemcpSet = mbo.getMboSet("UDITEMCP");
			if (!itemcpSet.isEmpty() && itemcpSet.count() > 0) {
				MboRemote itemcp = itemcpSet.getMbo(0);
				String abctype = itemcp.getString("abctype");
				if ("A".equalsIgnoreCase(abctype)) {
					sql += " and value in ('GUASPA')";
				} else if ("B".equalsIgnoreCase(abctype)) {
					sql += " and value in ('ARRUSE2','MONTHB1','MONTHB3','MONTHB6','MONTHB0')";
				} else if ("C".equalsIgnoreCase(abctype)) {
					sql += " and value in ('ARRUSE1','MONTHA1','MONTHA2','MONTHA3','MONTHA4','MONTHA5','MONTHA6')";
				} else if ("D".equalsIgnoreCase(abctype)) {
					sql += " and value in ('ARRUSE1')";
				} else if ("E".equalsIgnoreCase(abctype)) {
					sql += " and value in ('GUASPA','ARRUSE1')";
				}
			}
		} else {
			sql = "domainid = 'UDESTTIME'";
		}
		setListCriteria(sql);
		return super.getList();
	}
}
