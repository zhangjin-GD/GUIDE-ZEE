package guide.app.inventory;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.app.inventory.FldInvUseLineItemNum;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.SqlFormat;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldInvUseLineItemNum extends FldInvUseLineItemNum {

	public UDFldInvUseLineItemNum(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void init() throws MXException, RemoteException {
		super.init();

		MboRemote mbo = getMboValue().getMbo();
		setOrderRequired(mbo, false);
		setAssetRequired(mbo);
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
		MboRemote owner = mbo.getOwner();
		SqlFormat sql = new SqlFormat(mbo, "location=:1 and siteid=:tositeid");
		sql.setObject(1, "LOCATIONS", "LOCATION", mbo.getString("fromstoreloc"));
		MboSetRemote locationSet = mbo.getMboSet("$storeroomloc", "LOCATIONS", sql.format());
		if (locationSet != null && !locationSet.isEmpty()) {
			MboRemote location = locationSet.getMbo(0);
			String controlacc = location.getString("controlacc");
			mbo.setValue("gldebitacct", controlacc, 2L);
		}

		setOrderRequired(mbo, true);

//		if (owner != null) {
//			MboSetRemote invbalSet = mbo.getMboSet("INVBALANCES_BINLOT");
//			if (!invbalSet.isEmpty() && invbalSet.count() > 0) {
//				MboRemote invbal = invbalSet.getMbo(0);
//				String apptype = owner.getString("udapptype");
//				int udmatrectransid = invbal.getInt("udmatrectransid");
//				double predicttaxprice = invbal.getDouble("udpredicttaxprice");
//				String udponum = invbal.getString("udponum");
//				if ("matusecs".equalsIgnoreCase(apptype)) {
//					String tax1code = owner.getString("udvendor.tax1code");
//					mbo.setValue("udtax1code", tax1code, 2L);
//					if (predicttaxprice > 0) {
//						mbo.setValue("udtotalprice", predicttaxprice, 2L);
//					}
//				}
//				mbo.setValue("udmatrectransid", udmatrectransid, 11L);
//				mbo.setValue("udponum", udponum, 11L);
//			}
//		}
		
		//ZEE- 库存转移，目标库房需要与原始库房保持一致107-115
		if (owner != null && owner instanceof UDInvUse) {
			String apptype = owner.getString("udapptype");
			if ("transferzee".equalsIgnoreCase(apptype) && owner.getString("udcompany").equalsIgnoreCase("ZEE")) {
				if(!owner.getString("fromstoreloc").equalsIgnoreCase("")){
					mbo.setValue("tostoreloc", owner.getString("fromstoreloc"), 2L);
				}
			}
		}
	}

	private void setAssetRequired(MboRemote mbo) throws RemoteException, MXException {
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String movementType = owner.getString("udmovementtype");
			String materialtype = CommonUtil.getValue(mbo, "ITEM", "udmaterialtype");
			if (movementType != null && materialtype != null) {
				if ((movementType.equalsIgnoreCase("204") || movementType.equalsIgnoreCase("205")
						|| movementType.equalsIgnoreCase("206") || movementType.equalsIgnoreCase("221"))) {
					mbo.setFieldFlag("assetnum", 128L, true);
				} else if (movementType.equalsIgnoreCase("207") && materialtype.equalsIgnoreCase("1005")) {
					mbo.setFieldFlag("assetnum", 128L, true);
				} else {
					mbo.setFieldFlag("assetnum", 128L, false);
				}
			} else {
				mbo.setFieldFlag("assetnum", 128L, false);
			}
		}
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
				if (setDefault)
					mbo.setValue("udbudgetnum",
							CommonUtil.getBudget(owner, sapMappingSet.getMbo(0).getString("buditemnum")), 11L);
				if (!mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
					if (mbo.getString("udbudgetnum") == null || mbo.getString("udbudgetnum").equalsIgnoreCase("")) {
						throw new MXApplicationException("guide", "1015");
					}
				}
				boolean hasOrderType = sapMappingSet.getMbo(0).getBoolean("hasordertype");
				String hadOrderType = CommonUtil.getDefSapOrderType(sapMappingSet.getMbo(0).getInt("udsapmappingid"),
						owner.getString("udcompany"), "ordertype");
				if (hadOrderType != null) {
					hasOrderType = true;
				}
				if (hasOrderType) {
					mbo.setFieldFlag("udordertype", 128L, true);
					if (setDefault)
						setOrderType(sapMappingSet.getMbo(0));
				} else {
					mbo.setFieldFlag("udordertype", 128L, false);
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
			mbo.setValue("udordertype", ordertype, 11L);
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
