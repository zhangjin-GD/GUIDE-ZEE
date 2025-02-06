package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldPrlineProjectNum extends MAXTableDomain {
	/** 
	 * ZEE - 采购申请capex&project-code
	 * 2025-1-21  13:17  
	 */
	public UDFldPrlineProjectNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDPROJECT", "UDPROJECTNUM=:" + thisAttr);
		String[] FromStr = { "UDPROJECTNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();//PRLINE
		MboRemote owner = mbo.getOwner();//PR
		if(owner != null && owner instanceof UDPR){
			if(owner.getString("udcompany").equalsIgnoreCase("ZEE")){
			setListCriteria("status = 'APPR' and udcompany = 'ZEE' ");
			}
		}
		return super.getList();
	}
}
