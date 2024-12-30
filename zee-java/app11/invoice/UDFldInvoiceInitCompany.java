package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 *@function:发票中间表选择供应商
 *@author:zj
 *@date:2023-10-12 17:22:39
 *@modify:
 */
public class UDFldInvoiceInitCompany extends MAXTableDomain {

	public UDFldInvoiceInitCompany(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("COMPANIES", "COMPANY =:" + thisAttr);
		String[] FromStr = { "COMPANY" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	@Override
	public MboSetRemote getList() throws MXException, RemoteException {  
		String sql="1=1";
		setListCriteria(sql);
		return super.getList();
	}
	
}
