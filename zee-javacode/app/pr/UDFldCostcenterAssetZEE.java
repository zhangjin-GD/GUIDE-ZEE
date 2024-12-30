package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 * 	ZEE-设备的成本中心可选
 * 2024-04-01 13:39:13
 */
public class UDFldCostcenterAssetZEE extends MAXTableDomain{
	public UDFldCostcenterAssetZEE(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ASSET", "UDCOSTCENTER=:" + thisAttr);
		String[] FromStr = { "UDCOSTCENTER" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote list = super.getList();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner!=null) {
			String udcompany = owner.getString("udcompany");
			if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
				list.setWhere("udcompany = 'ZEE' and udcostcenter is not null");
				list.reset();
			}
		}
		else{
			list.setWhere("udcompany = 'ZEE' and udcostcenter is not null");
			list.reset();
		}
		return list;
	}
}
