package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWpLaborCode extends MAXTableDomain {

	public FldWpLaborCode(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("labor", "laborcode = :"+thisAttr);
		String[] FromStr = { "laborcode" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "status in (select value from synonymdomain where domainid='LABORSTATUS' and maxvalue='ACTIVE')";
		setListCriteria(sql);
		return super.getList();
	}
}
