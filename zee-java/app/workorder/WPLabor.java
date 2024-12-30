package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class WPLabor extends Mbo implements MboRemote {

	public WPLabor(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof UDWO) {
			String wonum = parent.getString("wonum");
			this.setValue("wonum", wonum, 11L);
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		
		MboRemote owner = this.getOwner();
		if(owner != null){
			String laborCode = this.getString("laborcode");
			String laborName = this.getString("LABORCODE.displayname");
			String oldPersonIdList = owner.getString("udpersonidlist");
			String oldPersonNameList = owner.getString("udpersonnamelist");
			if(this.toBeDeleted()){
				if(oldPersonIdList.startsWith(laborCode)){
					oldPersonIdList = oldPersonIdList.replace(laborCode+",", "");
					oldPersonNameList = oldPersonNameList.replace(laborName+",", "");
				}else{
					oldPersonIdList = oldPersonIdList.replace(","+laborCode, "");
					oldPersonNameList = oldPersonNameList.replace(","+laborName, "");
				}
			}else if(this.toBeAdded()){
				if(oldPersonIdList != null && !oldPersonIdList.equalsIgnoreCase("")){
					oldPersonIdList += ","+laborCode;
					oldPersonNameList += ","+laborName;
				}else{
					oldPersonIdList += laborCode;
					oldPersonNameList += laborName;
				}
				
			}
			owner.setValue("udpersonidlist", oldPersonIdList, 11L);
			owner.setValue("udpersonnamelist", oldPersonNameList, 11L);
		}
		
//		MboRemote parent = this.getOwner();
//		if (parent != null && parent instanceof UDWO) {
//			String worktype = parent.getString("worktype");
//			// 状态维修工单，预防性维护工单
//			if ("CM".equalsIgnoreCase(worktype) || "PM".equalsIgnoreCase(worktype)) {
//				boolean isflag = false;
//				MboSetRemote wpSet = this.getThisMboSet();
//				for (int i = 0; wpSet.getMbo(i) != null; i++) {
//					MboRemote wpMbo = wpSet.getMbo(i);
//					if (!wpMbo.toBeDeleted()) {
//						int personType = wpMbo.getInt("laborcode.udtype");
//						if (personType == 2) {// 外派人员
//							isflag = true;
//							break;
//						}
//					}
//				}
//				if (isflag) {
//					parent.setValue("udrepairtype", "OUTSIDE", 11L);// 外修
//				} else {
//					parent.setValue("udrepairtype", "INSIDE", 11L);// 内修
//				}
//			}
//		}
	}

}
