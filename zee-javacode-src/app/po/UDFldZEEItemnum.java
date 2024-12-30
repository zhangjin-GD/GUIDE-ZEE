package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 *@function:ZEE-外协订单行选择zee的物资编码
 *@author:zj
 *@date:2024-01-05 10:05:34
 *@modify:
 */
public class UDFldZEEItemnum extends MAXTableDomain {

	public UDFldZEEItemnum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ITEM", "ITEMNUM=:" + thisAttr);
		String[] FromStr = { "ITEMNUM" };
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
				list.setWhere(" itemnum in (select itemnum from uditemcp where udcompany='ZEE') ");
				list.reset();
			}
		}
		return list;
	}
}
