package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 * 
 * @function:ZEE-泽港工单选择吊具
 * @date:2023-07-25 16:23:33
 */

public class UDFldWOTLnumZEE extends MAXTableDomain {

	public UDFldWOTLnumZEE(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ASSET", "ASSETNUM =:" + thisAttr);
		String[] FromStr = { "ASSETNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		String sql = " 1=1 ";
		String udcompany = mbo.getString("udcompany");
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			sql=" udcompany='"+udcompany+"' and udassettypecode in ('SQ','SY','SR','SM','SOF','SE','LH','LB','GB','FB') ";
		}
		setListCriteria(sql);
		return super.getList();
	}
	
}
