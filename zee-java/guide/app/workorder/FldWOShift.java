package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWOShift extends MAXTableDomain {

	public FldWOShift(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDSIGNSHIFT", "SIGNSHIFTNUM =:" + thisAttr);
		String[] FromStr = { "SIGNSHIFTNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("udcompany = :udcompany");
		return super.getList();
	}
}
