package guide.iface.sap;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldSapHeaderZtran extends MboValueAdapter{
	
	public FldSapHeaderZtran(MboValue mbv) {
		super(mbv);
	}
	
	@Override
	public void init() throws MXException, RemoteException {
		super.init();
		MboRemote mbo = this.getMboValue().getMbo();
		String ztran = mbo.getString("ztran");
		if(ztran != null && ztran.indexOf("10") == 0) {
			mbo.setFieldFlag("lifnr", 7L, false);
			mbo.setFieldFlag("lifnr", 128L, true);
		}else {
			mbo.setFieldFlag("lifnr", 128L, false);
			mbo.setFieldFlag("lifnr", 7L, true);
		}
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String ztran = mbo.getString("ztran");
		if(ztran != null && ztran.indexOf("10") == 0) {
			mbo.setFieldFlag("lifnr", 7L, false);
			mbo.setFieldFlag("lifnr", 128L, true);
		}else {
			mbo.setFieldFlag("lifnr", 128L, false);
			mbo.setFieldFlag("lifnr", 7L, true);
		}
	}

}
