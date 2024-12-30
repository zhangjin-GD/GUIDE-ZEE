package guide.app.gjobplan;

import java.rmi.RemoteException;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class UDGjobPlan extends UDMbo implements MboRemote {

	public UDGjobPlan(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		
		try {
			String[] attrs = {"assettype", "worktype"};
			MboSetRemote gpmSet = getMboSet("UDGPM");
			if(!gpmSet.isEmpty() && gpmSet.count() > 0){
				setFieldFlag(attrs, 7L, true);
			}else {
				setFieldFlag(attrs, 7L, false);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		setValue("status", "INACTIVE", 11L);// 状态
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if(toBeAdded() || isModified("assettype") || isModified("worktype")){
			String prefix = CommonUtil.getAbbrCompany(getString("udcompany")) + "-" + getString("assettype") + "-" + getString("worktype");
			String keyNum = CommonUtil.autoKeyNum("UDGJOBPLAN", "gjpnum", prefix, "", 4);
			this.setValue("gjpnum", keyNum, 11L);// 自动编号
		}
	}

	@Override
	public void delete(long accessModifier) throws MXException, RemoteException {
		super.delete(accessModifier);
		this.getMboSet("UDGJOBTASK").deleteAll(2L);
	}

	@Override
	public void undelete() throws MXException, RemoteException {
		super.undelete();
		this.getMboSet("UDGJOBTASK").undeleteAll();
	}
	
}
