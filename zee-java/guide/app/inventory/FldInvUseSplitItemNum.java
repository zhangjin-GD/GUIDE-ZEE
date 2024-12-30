package guide.app.inventory;

import java.rmi.RemoteException;

import guide.app.common.CommonUtil;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldInvUseSplitItemNum extends MAXTableDomain {

	public FldInvUseSplitItemNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("item", "itemnum=:" + thisAttr);
		String[] FromStr = { "itemnum" };
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
				MboSetRemote woInvBalSet = owner.getMboSet("INVENTORYOUTWO");
				String completeWhere = woInvBalSet.getCompleteWhere();
				listSet.setWhere("itemnum in (select itemnum from inventory where " + completeWhere + ")");
			} else if ("MATUSEOT".equalsIgnoreCase(appType)) {// 非工单领料
				MboSetRemote woInvBalSet = owner.getMboSet("INVENTORYOUTOT");
				String completeWhere = woInvBalSet.getCompleteWhere();
				listSet.setWhere("itemnum in (select itemnum from invbalances where " + completeWhere + ")");
			}
		}
		return super.getList();
	}

	@Override
	public void validate() throws RemoteException, MXException {
		super.validate();
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDInvUse) {
			String sql = "1=2";
			String appType = owner.getString("udapptype");
			String movementType = owner.getString("udmovementtype");
			if (appType != null && ("MATUSEWO".equalsIgnoreCase(appType) || "MATUSEOT".equalsIgnoreCase(appType)
					|| "MATUSECS".equalsIgnoreCase(appType))) {
				sql = getMovementSql(appType, movementType);
				MboSetRemote sapMappingSet = mbo.getMboSet("$UDSAPMAPPING", "UDSAPMAPPING", sql);
				if (sapMappingSet.isEmpty())
					throw new MXApplicationException("guide", "1019");
			}
		}
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		mbo.setValue("description", mbo.getString("item.description"), 11L);
		setOrderRequired(mbo, true);
	}

	private void setOrderRequired(MboRemote mbo, boolean setDefault) throws RemoteException, MXException {
		String sql = "1=2";
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String appType = owner.getString("udapptype");
			String movementType = owner.getString("udmovementtype");
			if (appType != null && ("MATUSEWO".equalsIgnoreCase(appType) || "MATUSEOT".equalsIgnoreCase(appType)
					|| "MATUSECS".equalsIgnoreCase(appType))) {
				sql = getMovementSql(appType, movementType);
			}
			MboSetRemote sapMappingSet = mbo.getMboSet("$UDSAPMAPPING", "UDSAPMAPPING", sql);
			if (!sapMappingSet.isEmpty() && sapMappingSet.count() > 0) {

				boolean hasOrderType = sapMappingSet.getMbo(0).getBoolean("hasordertype");
				String hadOrderType = CommonUtil.getDefSapOrderType(sapMappingSet.getMbo(0).getInt("udsapmappingid"),
						owner.getString("udcompany"), "ordertype");
				if (hadOrderType != null) {
					hasOrderType = true;
				}
				if (hasOrderType) {
					mbo.setFieldFlag("ordertype", 128L, true);
					if (setDefault)
						setOrderType(sapMappingSet.getMbo(0));
				} else {
					mbo.setFieldFlag("ordertype", 128L, false);
				}
			}
		}
	}

	private void setOrderType(MboRemote sapMapping) throws RemoteException, MXException {
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String company = owner.getString("udcompany");
		String ordertype = CommonUtil.getDefSapOrderType(sapMapping.getInt("udsapmappingid"), company, "ordertype");
		if (ordertype != null) {
			mbo.setValue("ordertype", ordertype, 11L);
		}
	}

	private String getMovementSql(String appType, String movementType) throws RemoteException, MXException {
		String sql = "1=2";
		MboRemote mbo = getMboValue().getMbo();
		if (appType.equalsIgnoreCase("MATUSEOT")) {// 非工单领料单
			sql = "materialtype='" + CommonUtil.getValue(mbo, "ITEM", "udmaterialtype") + "' and movementtype='"
					+ movementType + "' and matot=1 and isreturn=0 and hascostcenter=1 and hasvendor=0";
		} else if (appType.equalsIgnoreCase("MATRETOT")) {// 非工单退料
			sql = "materialtype='" + CommonUtil.getValue(mbo, "ITEM", "udmaterialtype") + "' and movementtype='"
					+ movementType + "' and matot=1 and isreturn=1 and hascostcenter=1 and hasvendor=0";
		} else if (appType.equalsIgnoreCase("MATUSEWO") || appType.equalsIgnoreCase("MATUSECS")) {// 工单领料
			sql = "materialtype='" + CommonUtil.getValue(mbo, "ITEM", "udmaterialtype") + "' and movementtype='"
					+ movementType + "' and matwo=1 and isreturn=0 and hascostcenter=1 and hasvendor=0";
		} else if (appType.equalsIgnoreCase("MATRETWO") || appType.equalsIgnoreCase("MATRETCS")) {// 工单退料
			sql = "materialtype='" + CommonUtil.getValue(mbo, "ITEM", "udmaterialtype") + "' and movementtype='"
					+ movementType + "' and matwo=1 and isreturn=1 and hascostcenter=1 and hasvendor=0";
		} else if ("TRANSFER".equalsIgnoreCase(appType)) {// 库存转移
//			sql = " value in('211')";
		}
		return sql;
	}
}
