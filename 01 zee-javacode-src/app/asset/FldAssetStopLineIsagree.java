package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldAssetStopLineIsagree extends MboValueAdapter{
	
	public FldAssetStopLineIsagree(MboValue mbv) {
		super(mbv);
	}
	
	public void init() throws RemoteException, MXException {
		super.init();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		
		MboRemote mbo = this.getMboValue().getMbo();
		String isAgree = mbo.getString("isagree");
		String[] oacAttrs = {"actstarttime", "actendtime"};
		if(isAgree != null && isAgree.equalsIgnoreCase("Y")){
			mbo.setValue("checkby", mbo.getUserInfo().getPersonId(), 11L);
			mbo.setFieldFlag(oacAttrs, 7L, false);
			mbo.setFieldFlag(oacAttrs, 128L, true);
		}else {
			mbo.setValue("checkby", mbo.getUserInfo().getPersonId(), 11L);
			mbo.setFieldFlag(oacAttrs, 128L, false);
			mbo.setFieldFlag(oacAttrs, 7L, true);
		}
		
	}
	
	
}
