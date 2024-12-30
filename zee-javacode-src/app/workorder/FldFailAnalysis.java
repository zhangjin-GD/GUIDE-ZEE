package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldFailAnalysis extends MAXTableDomain{
	

	public FldFailAnalysis(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ALNDOMAIN", "domainid='UDFAILANALYSIS' and value =:" + thisAttr);
		String[] FromStr = { "value" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "domainid='UDFAILANALYSIS'";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void validate() throws MXException, RemoteException {
//		super.validate();
	}

}
