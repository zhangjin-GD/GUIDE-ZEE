package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.FldInvUseFromStoreloc;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldInvUseFromStoreloc extends FldInvUseFromStoreloc {

	public UDFldInvUseFromStoreloc(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		Mbo mbo = this.getMboValue().getMbo();
		String apptype = mbo.getString("udapptype");
		if ("matusecs".equalsIgnoreCase(apptype) || "matretcs".equalsIgnoreCase(apptype)) {
			setListCriteria("udissap =1 and udisconsignment = 1");
		} else if ("transfer".equalsIgnoreCase(apptype)) {
			setListCriteria("udissap =1 and udisconsignment = 0");
		} else if("transferzee".equalsIgnoreCase(apptype) && mbo.getString("udcompany").equalsIgnoreCase("ZEE")){
			//ZEE - 库存转移，原始库房可选择ZEE所有库房26-29
			setListCriteria("udcompany = 'ZEE' and type = 'STOREROOM'");
		} else {
			setListCriteria("udisconsignment = 0");
		} 
		return super.getList();
	}

	public void init() throws RemoteException, MXException {
		super.init();

		MboRemote mbo = getMboValue().getMbo();
		setReadonly(mbo);
	}

	private void setReadonly(MboRemote mbo) throws RemoteException, MXException {
		MboSetRemote invuselineSet = mbo.getMboSet("INVUSELINE");
		if (!invuselineSet.isEmpty() && invuselineSet.count() > 0) {
			mbo.setFieldFlag("fromstoreloc", 7L, true);
		} else {
			mbo.setFieldFlag("fromstoreloc", 7L, false);
		}
	}
}
