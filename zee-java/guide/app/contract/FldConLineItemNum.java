package guide.app.contract;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldConLineItemNum extends MAXTableDomain {

	public FldConLineItemNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ITEM", "ITEMNUM=:" + thisAttr);
		String[] FromStr = { "ITEMNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("udisfix=0 and status in ('ACTIVE')");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboSetRemote itemSet = mbo.getMboSet("ITEM");
		if (!itemSet.isEmpty() && itemSet.count() > 0) {
			MboRemote item = itemSet.getMbo(0);
			mbo.setValue("description", item.getString("description"), 11L);
		}
	}
}
