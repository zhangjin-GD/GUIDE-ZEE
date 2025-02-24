package guide.app.inventory;

import guide.app.common.CommonUtil;
import guide.app.po.UDPO;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldMatrecBudgetNum extends MAXTableDomain{
	/** 
	 * ZEE - 采购入库capex&BudgetNum
	 * 2025-2-19 13:17  
	 */
	public UDFldMatrecBudgetNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDBUDGET", "BUDGETNUM=:" + thisAttr);
		String[] FromStr = { "BUDGETNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();//matrec
		MboRemote owner = mbo.getOwner();//PO
		if(owner != null){
			if(owner.getString("udcompany").equalsIgnoreCase("ZEE")){
			setListCriteria("status = 'APPR' and udcompany = 'ZEE' and year='"+CommonUtil.getCurrentDateFormat("yyyy")+"'");
			}
		}
		return super.getList();
	}
}
