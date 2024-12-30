package guide.app.failclass;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldFailclassAsset extends MAXTableDomain {

	public FldFailclassAsset(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDFAILCLASS", "failclassnum = :" + thisAttr);
		String[] FromStr = { "failclassnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "type = 'ASSET'";
		setListCriteria(sql);
		return super.getList();
	}
}
