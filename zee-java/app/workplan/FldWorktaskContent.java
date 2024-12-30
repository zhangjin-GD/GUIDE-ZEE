package guide.app.workplan;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWorktaskContent extends MAXTableDomain{
	

	public FldWorktaskContent(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDWOREMAIN", "status!='COMP' and solvewonum is null and assetnum=:assetnum and description =:" + thisAttr);
		String[] FromStr = { "description" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "status!='COMP' and solvewonum is null and assetnum=:assetnum";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void validate() throws MXException, RemoteException {
//		super.validate();
	}

}
