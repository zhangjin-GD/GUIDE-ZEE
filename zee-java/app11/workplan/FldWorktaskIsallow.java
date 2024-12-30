package guide.app.workplan;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWorktaskIsallow extends MboValueAdapter{
	
	public FldWorktaskIsallow(MboValue mbv) {
		super(mbv);
	}
	
	@Override
	public void init() throws MXException, RemoteException {
		super.init();
		MboRemote mbo = this.getMboValue().getMbo();
		String isAllow = mbo.getString("isallow");
		if(isAllow != null && isAllow.equalsIgnoreCase("Y")){
			mbo.setFieldFlag("actualstartdate", 128L, true);
			mbo.setFieldFlag("actualenddate", 128L, true);
		}else if(isAllow != null && isAllow.equalsIgnoreCase("N")){
			mbo.setFieldFlag("actualstartdate", 128L, false);
			mbo.setFieldFlag("actualenddate", 128L, false);
		}
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String isAllow = mbo.getString("isallow");
		if(isAllow != null && isAllow.equalsIgnoreCase("Y")){
			mbo.setFieldFlag("actualstartdate", 128L, true);
			mbo.setFieldFlag("actualenddate", 128L, true);
		}else if(isAllow != null && isAllow.equalsIgnoreCase("N")){
			mbo.setFieldFlag("actualstartdate", 128L, false);
			mbo.setFieldFlag("actualenddate", 128L, false);
		}
	}

}
