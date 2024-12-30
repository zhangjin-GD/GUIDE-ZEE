package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPrser extends MAXTableDomain {

	public FldPrser(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("PR", "PRNUM =:" + thisAttr);
		String[] FromStr = { "PRNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("udapptype = 'PRSER' and status = 'APPR'");
		return super.getList();
	}
}
