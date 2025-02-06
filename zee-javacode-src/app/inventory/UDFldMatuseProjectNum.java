package guide.app.inventory;


import guide.app.workorder.UDWO;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldMatuseProjectNum extends MAXTableDomain{
	/** 
	 * ZEE - 采购申请capex&project-code
	 * 2025-1-21  13:17  
	 */
	public UDFldMatuseProjectNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDPROJECT", "UDPROJECTNUM=:" + thisAttr);
		String[] FromStr = { "UDPROJECTNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();//MATUSETRANS
		MboRemote owner = mbo.getOwner();//WO
		if(owner != null && owner instanceof UDWO){
			if(owner.getString("udcompany").equalsIgnoreCase("ZEE")){
			setListCriteria("status = 'APPR' and udcompany = 'ZEE' ");
			}
		}
		return super.getList();
	}
}
