package guide.app.workorder;


import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


public class FldWoemrptAssettypeCode extends MAXTableDomain {
	
	public FldWoemrptAssettypeCode(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDASSETTYPE", "parent is null and code=:" + thisAttr);
		String[] FromStr = { "code" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "parent is null";
		MboRemote mbo = this.getMboValue().getMbo();
		String appName = mbo.getThisMboSet().getApp();
		if (appName != null && appName.equalsIgnoreCase("UDWOEMRPT")) {
			sql = "parent is null and isemrpt=1";
		}
		setListCriteria(sql);
		return super.getList();
	}
	
	public void validate() throws RemoteException, MXException {
		super.validate();
		
	}
	
	public void action() throws RemoteException, MXException {
		super.action();
//		MboRemote mbo = this.getMboValue().getMbo();
//		String assetTypeCode = mbo.getString("udassettypecode");
//		String workType = mbo.getString("worktype");
//		if(assetTypeCode != null && assetTypeCode.equalsIgnoreCase("OT") 
//				&& workType != null && workType.equalsIgnoreCase("EM")){
//			mbo.setValue("worktype", "SW", 2L);
//		}else if(assetTypeCode != null && !assetTypeCode.equalsIgnoreCase("OT") 
//				&& workType != null && workType.equalsIgnoreCase("SW")){
//			mbo.setValue("worktype", "EM", 2L);
//		}
	}
	
}