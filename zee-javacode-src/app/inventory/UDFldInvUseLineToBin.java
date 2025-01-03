package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.FldInvUseLineToBin;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldInvUseLineToBin extends FldInvUseLineToBin{

	public UDFldInvUseLineToBin(MboValue mbv) throws MXException {
		super(mbv);
	}
	/**
	 * ZEE - 库存转移：目标批次与原始批次在只读情况下，保持一致
	 * 2025/1/2 14:10
	 */
	public void action() throws MXException, RemoteException{
		super.action();
		MboRemote mbo = getMboValue().getMbo();//INVUSELINE
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDInvUse && !this.getMboValue().isNull()){
			String appType = owner.getString("udapptype");
			if ("TRANSFERZEE".equalsIgnoreCase(appType)) {
				if (!mbo.isNull("tobin")) {
					String fromlot = mbo.getString("fromlot");
					mbo.setValue("tolot", fromlot, 11L);
				}
			}
		}
	}

}
