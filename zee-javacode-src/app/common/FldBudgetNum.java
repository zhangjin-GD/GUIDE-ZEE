package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldBudgetNum extends MAXTableDomain {

	public FldBudgetNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDBUDGET", "BUDGETNUM=:" + thisAttr);
		String[] FromStr = { "BUDGETNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote list = super.getList();
		MboRemote mbo = this.getMboValue().getMbo(); //PRLINE
		MboRemote appmbo = mbo.getOwner(); //PR
		if(appmbo == null) {
			appmbo = mbo;
		}
		String company = appmbo.getString("udcompany");
		String department = appmbo.getString("uddept");
		String office = appmbo.getString("udofs");
		if(office == null || office.equalsIgnoreCase("")){
			office = "null";
		}
		list.setWhere("status = 'APPR' and year='"+CommonUtil.getCurrentDateFormat("yyyy")+"'"
				+ " and udcompany='"+company+"' and nvl(uddept,'"+department+"')='"+department+"' and nvl(udofs,'"+office+"')='"+office+"'");
		list.reset();
		if(company!=null && company.equalsIgnoreCase("ZEE")){
			list.setWhere("status = 'APPR' and year='"+CommonUtil.getCurrentDateFormat("yyyy")+"'"
				+ " and udcompany='"+company+"' and uddept = '"+department+"' ");
			list.reset();
		}
		return list;
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		mbo.setValueNull("udprojectnum", 2L);
	}
}
