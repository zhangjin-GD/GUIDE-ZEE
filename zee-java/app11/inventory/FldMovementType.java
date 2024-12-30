package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldMovementType extends MAXTableDomain {

	public FldMovementType(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ALNDOMAIN", "domainid='MOVEMENTTYPE' and value =:" + thisAttr);
		String[] FromStr = { "value" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		String sql = "1=2";
		String appType = mbo.getString("udapptype");
		if(appType != null){
			if (appType.equalsIgnoreCase("MATUSEOT")) {// 非工单领料单
				sql = "domainid='MOVEMENTTYPE' and value in(select movementtype from udsapmapping where matot=1 and isreturn=0 and hascostcenter=1 and hasvendor=0)";
			}else if (appType.equalsIgnoreCase("MATRETOT")) {// 非工单退料
				sql = "domainid='MOVEMENTTYPE' and value in(select movementtype from udsapmapping where matot=1 and isreturn=1 and hascostcenter=1 and hasvendor=0)";
			}else if (appType.equalsIgnoreCase("MATUSEWO") || appType.equalsIgnoreCase("MATUSECS")) {// 工单领料
				sql = "domainid='MOVEMENTTYPE' and value in(select movementtype from udsapmapping where matwo=1 and isreturn=0 and hascostcenter=1 and hasvendor=0)";
			}else if (appType.equalsIgnoreCase("MATRETWO") || appType.equalsIgnoreCase("MATRETCS")) {// 工单退料
				sql = "domainid='MOVEMENTTYPE' and value in(select movementtype from udsapmapping where matwo=1 and isreturn=1 and hascostcenter=1 and hasvendor=0)";
			}else if ("TRANSFER".equalsIgnoreCase(appType)) {// 库存转移
	//			sql = " value in('211')";
			}
		}
		setListCriteria(sql);
		return super.getList();
	}

	public void validate() throws RemoteException, MXException {
		super.validate();
		
		MboRemote mbo = getMboValue().getMbo();
		String appType = mbo.getString("udapptype");
		String movementType = mbo.getString("udmovementtype");
		checkMovementType(appType, movementType);
		
	}
	
	public void init() throws RemoteException, MXException {
		super.init();
		
		MboRemote mbo = getMboValue().getMbo();
		setReadonly(mbo);
	}
	
	private void setReadonly(MboRemote mbo) throws RemoteException, MXException {
		MboSetRemote invuselineSet = mbo.getMboSet("INVUSELINE");
		if(!invuselineSet.isEmpty() && invuselineSet.count() > 0){
			mbo.setFieldFlag("udmovementtype", 7L, true);
		}else {
			mbo.setFieldFlag("udmovementtype", 7L, false);
		}
	}

	private void checkMovementType(String appType, String movementType) throws RemoteException, MXException {
		String sql = "1=2";
		if(appType != null){
			sql = getMovementSql(appType, movementType);
		}
		MboSetRemote sapMappingSet = MXServer.getMXServer().getMboSet("UDSAPMAPPING", MXServer.getMXServer().getSystemUserInfo());
		sapMappingSet.setWhere(sql);
		if(sapMappingSet.isEmpty()){
			sapMappingSet.close();
			Object params[] = { "提示：无效的移动类型!"};
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		}
		sapMappingSet.close();
	}
	
	private String getMovementSql(String appType, String movementType) {
		String sql = "1=2";
		if (appType.equalsIgnoreCase("MATUSEOT")) {// 非工单领料单
			sql = "movementtype='"+movementType+"' and matot=1 and isreturn=0 and hascostcenter=1 and hasvendor=0";
		}else if (appType.equalsIgnoreCase("MATRETOT")) {// 非工单退料
			sql = "movementtype='"+movementType+"' and matot=1 and isreturn=1 and hascostcenter=1 and hasvendor=0";
		}else if (appType.equalsIgnoreCase("MATUSEWO")||appType.equalsIgnoreCase("MATUSECS")) {// 工单领料
			sql = "movementtype='"+movementType+"' and matwo=1 and isreturn=0 and hascostcenter=1 and hasvendor=0";
		}else if (appType.equalsIgnoreCase("MATRETWO")||appType.equalsIgnoreCase("MATRETCS")) {// 工单退料
			sql = "movementtype='"+movementType+"' and matwo=1 and isreturn=1 and hascostcenter=1 and hasvendor=0";
		}else if ("TRANSFER".equalsIgnoreCase(appType)) {// 库存转移
//			sql = " value in('211')";
		}
		return sql;
	}
	
}
