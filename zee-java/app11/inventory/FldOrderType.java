package guide.app.inventory;


import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldOrderType extends MAXTableDomain {

	public FldOrderType(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ALNDOMAIN", "domainid='ORDERTYPE' and value =:" + thisAttr);
		String[] FromStr = { "value" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		String sql = "1=2";
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String appType = owner.getString("udapptype");
			String movementType = owner.getString("udmovementtype");
			String materialType = CommonUtil.getValue(mbo, "ITEM", "udmaterialtype");
			if(appType != null){
				sql = getOrderSql(appType, movementType, materialType);
			}
		}
		setListCriteria(sql);
		return super.getList();
	}

	private String getOrderSql(String appType, String movementType, String materialType) throws RemoteException, MXException {
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String sql = "1=2";
		if (owner != null) {
			String company = owner.getString("udcompany");
			if (appType.equalsIgnoreCase("MATUSEOT")) {// 领料单
				sql = "domainid='ORDERTYPE' and value in(select ordertype from udsapordertype where parent in(select udsapmappingid from udsapmapping where movementtype='"+movementType+"' and materialType='"+materialType+"' and matot=1 and isreturn=0 and hascostcenter=1 and hasvendor=0) and udcompany='"+company+"')";
			}else if (appType.equalsIgnoreCase("MATRETOT")) {// 退料单
				sql = "domainid='ORDERTYPE' and value in(select ordertype from udsapordertype where parent in(select udsapmappingid from udsapmapping where movementtype='"+movementType+"' and materialType='"+materialType+"' and matot=1 and isreturn=1 and hascostcenter=1 and hasvendor=0) and udcompany='"+company+"')";
			}else if (appType.equalsIgnoreCase("MATUSEWO") || appType.equalsIgnoreCase("MATUSECS")) {// 领料单
				sql = "domainid='ORDERTYPE' and value in(select ordertype from udsapordertype where parent in(select udsapmappingid from udsapmapping where movementtype='"+movementType+"' and materialType='"+materialType+"' and matwo=1 and isreturn=0 and hascostcenter=1 and hasvendor=0) and udcompany='"+company+"')";
			}else if (appType.equalsIgnoreCase("MATRETWO") || appType.equalsIgnoreCase("MATRETCS")) {// 退料单
				sql = "domainid='ORDERTYPE' and value in(select ordertype from udsapordertype where parent in(select udsapmappingid from udsapmapping where movementtype='"+movementType+"' and materialType='"+materialType+"' and matwo=1 and isreturn=1 and hascostcenter=1 and hasvendor=0) and udcompany='"+company+"')";
			}else if ("TRANSFER".equalsIgnoreCase(appType)) {// 库存转移
	//			sql = " value in('211')";
			}
		}
		return sql;
	}
	
	public void validate() throws RemoteException, MXException {
		super.validate();
		
//		MboRemote mbo = getMboValue().getMbo();
//		MboRemote Owner = mbo.getOwner();
//		String orderType = mbo.getString("udordertype");
//		if(Owner != null && orderType != null && !orderType.equalsIgnoreCase("") && Owner.getString("udcompany").equalsIgnoreCase("23K9NTTH")){
//			String funarea = CommonUtil.getValue(Owner, "UDDEPT", "funarea");
//			if (funarea != null && funarea.equalsIgnoreCase("1300") && orderType.equalsIgnoreCase("900106Q10014")) {//900106Q10002
//				throw new MXApplicationException("guide", "1016");
//			}else if(funarea != null && funarea.equalsIgnoreCase("1100") && orderType.equalsIgnoreCase("900106Q10013")){//900106Q10001
//				throw new MXApplicationException("guide", "1016");
//			}
//		}

	}
	
}
