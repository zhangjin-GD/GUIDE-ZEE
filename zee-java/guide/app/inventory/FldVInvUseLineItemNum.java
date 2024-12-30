package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldVInvUseLineItemNum extends MAXTableDomain {

	public FldVInvUseLineItemNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ITEM", "ITEMNUM=:" + thisAttr);
		String[] FromStr = { "ITEMNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDInvUse) {
			String appType = owner.getString("udapptype");
			if ("MATUSEWO".equalsIgnoreCase(appType) || "MATUSECS".equalsIgnoreCase(appType)) {// 工单领料
				MboSetRemote woInvBalSet = owner.getMboSet("INVBALANCESOUTWO");
				String completeWhere = woInvBalSet.getCompleteWhere();
				listSet.setWhere("itemnum in (select itemnum from invbalances where " + completeWhere + ")");
			} else if ("MATUSEOT".equalsIgnoreCase(appType)) {// 非工单领料
				MboSetRemote woInvBalSet = owner.getMboSet("INVBALANCESOUTOT");
				String completeWhere = woInvBalSet.getCompleteWhere();
				listSet.setWhere("itemnum in (select itemnum from invbalances where " + completeWhere + ")");
			}
		}
		return listSet;
	}
}
