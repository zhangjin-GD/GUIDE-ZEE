package guide.app.inventory;

import java.rmi.RemoteException;
import java.util.HashSet;

import psdi.app.inventory.InvUseLine;
import psdi.app.inventory.InvUseLineRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboValueInfo;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDInvUseLine extends InvUseLine implements InvUseLineRemote {

	private static boolean isHashSetLoaded = false;
	private static HashSet<String> skipFieldCopy = new HashSet<String>();

	public UDInvUseLine(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();

	}

	@Override
	public void initFieldFlagsOnMbo(String attrName) throws MXException {
		super.initFieldFlagsOnMbo(attrName);
	}

//	private void attributeReadonly(boolean state) throws RemoteException, MXException {
//		String[] attrMbo = { "itemnum", "frombin", "fromlot", "quantity", "udordertype", "udprojectnum", "enterby",
//				"actualdate" };
//		this.setFieldFlag(attrMbo, 7L, state);
//	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();

		MboRemote owner = this.getOwner();
		if (owner != null) {
			owner.setFieldFlag("udmovementtype", 7L, true);
			String udwonum = owner.getString("udwonum");
			String udprojectnum = owner.getString("udprojectnum");
			String udbudgetnum = owner.getString("udbudgetnum");
			this.setValue("wonum", udwonum, 11L);
			this.setValue("refwo", udwonum, 2L);
			this.setValue("udprojectnum", udprojectnum, 11L);
			this.setValue("udbudgetnum", udbudgetnum, 11L);
		}
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
		MboRemote owner = this.getOwner();
		if ((owner != null) && (owner instanceof UDInvUse)) {
			owner.setValue("changeby", getUserInfo().getPersonId(), 11L);
			owner.setValue("changedate", MXServer.getMXServer().getDate(), 11L);
            /**
             * ZEE - 库存转移：目标批次与原始批次在只读情况下，保持一致 64-71
             * 2025/1/6 11:10
             */
            String appType = owner.getString("udapptype");
            if(owner.getString("udcompany").equalsIgnoreCase("ZEE") &&  "TRANSFERZEE".equalsIgnoreCase(appType)){
            setValue("tolot", getString("fromlot"), 11L);
            }
		}
	}

	@Override
	protected boolean skipCopyField(MboValueInfo mvi) {
		if (!isHashSetLoaded) {
			loadSkipFieldCopyHashSet();
		}
		return skipFieldCopy.contains(mvi.getName());
	}

	private void loadSkipFieldCopyHashSet() {
		isHashSetLoaded = true;
		skipFieldCopy.add("INVUSENUM");
		skipFieldCopy.add("INVUSELINENUM");
		skipFieldCopy.add("ENTERBY");
		skipFieldCopy.add("ACTUALDATE");
		skipFieldCopy.add("QUANTITY");
		skipFieldCopy.add("UNITCOST");
		skipFieldCopy.add("LINECOST");
	}

}
