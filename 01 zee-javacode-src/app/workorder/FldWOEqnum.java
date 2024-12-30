package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWOEqnum extends MAXTableDomain {

	public FldWOEqnum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("asset", "assetnum = :" + thisAttr);
		String[] FromStr = { "assetnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		setListCriteria("status = 'ACTIVE' and udeqnum = :assetnum");
		return super.getList();
	}
}
