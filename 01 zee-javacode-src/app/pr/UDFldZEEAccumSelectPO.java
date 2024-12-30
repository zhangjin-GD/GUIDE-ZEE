package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


/**
 *@function:pur acc view选择PO弹框
 *@author:zj
 *@date:2024-10-17 16:59:50
 *@modify:
 */
public class UDFldZEEAccumSelectPO extends MAXTableDomain {

	public UDFldZEEAccumSelectPO(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("PO", "PONUM =:" + thisAttr);
		String[] FromStr = { "PONUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		if (mbo!=null) {
			String vendor = mbo.getString("vendor");
			String sql = " udcompany='ZEE' and status='WAPPR' and vendor='"+vendor+"' ";
			setListCriteria(sql);
		}
		return super.getList();
	}

}
