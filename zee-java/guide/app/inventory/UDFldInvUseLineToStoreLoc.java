package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.FldInvUseLineToStoreLoc;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldInvUseLineToStoreLoc extends FldInvUseLineToStoreLoc {

	public UDFldInvUseLineToStoreLoc(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		if (parent != null && parent instanceof UDInvUse) {
			String apptype = parent.getString("udapptype");
			if ("transfer".equalsIgnoreCase(apptype)) {
				setListCriteria("udissap =1 and udisconsignment = 0");
			} else if("transferzee".equalsIgnoreCase(apptype) && parent.getString("udcompany").equalsIgnoreCase("ZEE")){
				//ZEE - 库存转移，目标库房放大镜只能选原始库房26-29
				setListCriteria("location = '"+parent.getString("fromstoreloc")+"' ");
			}
		}
		return super.getList();
	}
}
