package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWOSettleLineWoNum extends MAXTableDomain {

	public FldWOSettleLineWoNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("workorder", "wonum=:" + thisAttr);
		String[] FromStr = { "wonum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "istask = 0 and status in ('COMP','CLOSE')";
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote woSet = mbo.getMboSet("WONUM");
		if (!woSet.isEmpty() && woSet.count() > 0) {
			MboRemote wo = woSet.getMbo(0);
			mbo.setValue("estcost", wo.getDouble("udestcost"), 2L);
		}
	}
}
