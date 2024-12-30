package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPrImpLineItemNum extends MAXTableDomain {

	public FldPrImpLineItemNum(MboValue mbv) {
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
		MboSetRemote invCostSet = mbo.getMboSet("UDINVCOST");
		if (!invCostSet.isEmpty() && invCostSet.count() > 0) {
			MboRemote invCost = invCostSet.getMbo(0);
			double avgcost = invCost.getDouble("avgcost");
			mbo.setValue("unitcost", avgcost, 2L);
		}

		MboSetRemote itemCpSet = mbo.getMboSet("UDITEMCP");
		if (!itemCpSet.isEmpty() && itemCpSet.count() > 0) {
			MboRemote itemCp = itemCpSet.getMbo(0);
			String remark = itemCp.getString("remarks");
			String storeloc = itemCp.getString("storeloc");
			if (storeloc != null && !storeloc.isEmpty()) {
				mbo.setValue("storeloc", storeloc, 2L);
			}
			mbo.setValue("remark", remark, 11L);
		}

		if (this.getMboValue().isNull()) {
			mbo.setValue("unitcost", 0, 2L);
			mbo.setValueNull("remark");
		}
	}
}
