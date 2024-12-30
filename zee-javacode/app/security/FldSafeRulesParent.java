package guide.app.security;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldSafeRulesParent extends MAXTableDomain {

	public FldSafeRulesParent(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDSAFERULES", "srnum=:" + thisAttr);
		String[] FromStr = { "srnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "srnum !=:srnum";
		setListCriteria(sql);
		return super.getList();
	}
}
