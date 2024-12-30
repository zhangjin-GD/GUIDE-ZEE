package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldMatReturnType extends MAXTableDomain {

	public FldMatReturnType(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDMATRETURNTYPE", "returntype1 =:" + thisAttr);
		String[] FromStr = { "returntype1" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("returntype =:returntype");
		return super.getList();
	}
}
