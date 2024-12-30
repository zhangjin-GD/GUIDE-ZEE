package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 * 	ZEE-如果服务，则手动选择costcenter
 * 2024-03-18 10:39:13
 */
public class UDFldCostcenterZEE extends MAXTableDomain{

	public UDFldCostcenterZEE(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDDEPT", "COSTCENTER=:" + thisAttr);
		String[] FromStr = { "COSTCENTER" };
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
				list.setWhere("deptnum in ('ZEE','ZEE01','ZEE02','ZEE03','ZEE05','ZEE06','ZEE07','ZEE10','ZEE11','ZEE12','ZEE13')");
				list.reset();
			}
		}
		return list;
	}
}
