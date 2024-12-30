package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 *@function:ZEE-非订单接收外协选择外协服务订单
 *@author:zj
 *@date:2024-01-05 09:51:54
 *@modify:
 */
public class UDFldZEEPonumWX extends MAXTableDomain {

	public UDFldZEEPonumWX(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("PO", "PONUM=:" + thisAttr);
		String[] FromStr = { "PONUM" };
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
				list.setWhere(" udcompany='ZEE' and status = 'APPR' and udapptype='POSER' ");
				list.reset();
			}
		}
		return list;
	}
}
