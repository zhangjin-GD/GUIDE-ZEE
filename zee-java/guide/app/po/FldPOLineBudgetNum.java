package guide.app.po;

import java.rmi.RemoteException;

import guide.app.common.CommonUtil;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPOLineBudgetNum extends MAXTableDomain {

	public FldPOLineBudgetNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDBUDGET", "BUDGETNUM=:" + thisAttr);
		String[] FromStr = { "BUDGETNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String sql = "status='APPR' and year='" + CommonUtil.getCurrentDateFormat("yyyy") + "'";
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			sql += " and udcompany='" + udcompany + "'";
		}
		setListCriteria(sql);
		return super.getList();
	}

	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String udbudgetnum = mbo.getString("udbudgetnum");
		MboSetRemote mboSet = mbo.getMboSet("prline");
		if (!mboSet.isEmpty() && mboSet.count() > 0) {
			mboSet.getMbo(0).setValue("udbudgetnum", udbudgetnum, 11L);
		}
	}
}
