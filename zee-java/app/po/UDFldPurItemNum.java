package guide.app.po;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurItemNum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldPurItemNum extends FldPurItemNum {

	public UDFldPurItemNum(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		/**
		 * ZEE-2024-01-05 10:02:37
		 */
		MboSetRemote list = super.getList();
		System.out.println("\n----1-5--list1--"+list);
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner!=null) {
			String udcompany = owner.getString("udcompany");
			if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
				String udzeeponumwx = mbo.getString("udzeeponumwx");
				list.setWhere("itemnum in (select udzeeitemnum from poline where ponum='"+udzeeponumwx+"')");
				System.out.println("\n----1-5--list5--"+list);
				list.reset();
				System.out.println("\n----1-5--list6--"+list);
			}
		}
		System.out.println("\n----1-5--list2--"+list);
		return list;
	}
	
	public void validate() throws RemoteException, MXException {
		super.validate();
		
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String udapptype = owner.getString("udapptype");
			MboSetRemote itemSet = mbo.getMboSet("ITEM");
			if (!itemSet.isEmpty() && itemSet.count() > 0) {
				MboRemote item = itemSet.getMbo(0);
				boolean isfix = item.getBoolean("udisfix");
				if ("POMAT".equalsIgnoreCase(udapptype) && isfix) {
					throw new MXApplicationException("guide", "1002");
				}else if("POFIX".equalsIgnoreCase(udapptype)  && !isfix){
					throw new MXApplicationException("guide", "1003");
				}
			}
		}

	}
	
	public void action() throws RemoteException, MXException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String itemnum = mbo.getString("itemnum");
		String materialType = CommonUtil.getValue(mbo, "ITEM", "udmaterialType");
		if(itemnum == null || itemnum.equalsIgnoreCase("")){
			mbo.setValue("issue", 1, 11L);
		}else{
			mbo.setValue("issue", 0, 11L);
			if(materialType != null && (materialType.equalsIgnoreCase("5201") || materialType.equalsIgnoreCase("2001"))){
				mbo.setValue("issue", 1, 11L);
				String costcenter = CommonUtil.getValue(mbo, "UDITEMCP", "costcenter");
				if(costcenter != null && !costcenter.equalsIgnoreCase("")){
					mbo.setValue("udcostcenter", costcenter, 11L);
				}
			}
		}
		mbo.setValue("udmaterialtype", materialType, 11L);
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			String uddept = owner.getString("uddept");
			if ("GR02PCT".equalsIgnoreCase(udcompany)) {
				MboSetRemote uditemcpSet = mbo.getMboSet("$UDITEMCP", "UDITEMCP", "udcompany='" + udcompany + "' and itemnum='" + itemnum + "'");
				if (!uditemcpSet.isEmpty() && uditemcpSet.count() > 0) {
					MboRemote uditemcp = uditemcpSet.getMbo(0);
					boolean isstock = uditemcp.getBoolean("isstock");
					if (isstock) {
						mbo.setValue("storeloc", "PCT-07", 2L);
					}
				}
				// GR02120002 技术部 GR02120010 IT 正常入库，其它部门即收即发
				if (!"GR02120002".equalsIgnoreCase(uddept) && !"GR02120010".equalsIgnoreCase(uddept)) {
					mbo.setValue("issue", 1, 2L);
				}
			}
		}
	}
	
}
