package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWOFailType extends MAXTableDomain {

	public FldWOFailType(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("FAILURELIST", "failurecode = :" + thisAttr);
		String[] FromStr = { "failurecode" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public void validate() throws MXException, RemoteException {
//		super.validate();
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		setListCriteria("parent is null");
		return super.getList();
	}
}
