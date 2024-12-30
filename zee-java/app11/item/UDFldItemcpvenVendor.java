package guide.app.item;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldItemcpvenVendor  extends MAXTableDomain{

	public UDFldItemcpvenVendor(MboValue mbv) {
		super(mbv);
		// TODO Auto-generated constructor stub
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("COMPANIES", "COMPANY=:" + thisAttr);
		String[] FromStr = { "COMPANY" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		MboRemote mbo = getMboValue().getMbo();
		listSet.setWhere("exists (select 1 from udcomptaxcode where company=companies.company and disabled=0 and udcompany='ZEE')");
		listSet.reset();
		return listSet;
	}
	
}
