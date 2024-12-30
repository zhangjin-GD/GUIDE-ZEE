package guide.app.jobplan;

import guide.app.common.FldComDept;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldJobplanOfs extends FldComDept {

	public FldJobplanOfs(MboValue mbv) {
		super(mbv);
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		
		MboRemote mbo = this.getMboValue().getMbo();
		String ofs = mbo.getString("udofs");
		if(ofs != null && !ofs.equalsIgnoreCase("")){
			MboSetRemote persongroupSet = mbo.getMboSet("$PERSONGROUP", "PERSONGROUP", "uddept ='" + ofs + "'");
			if (persongroupSet != null && !persongroupSet.isEmpty()) {
				MboRemote persongroup = persongroupSet.getMbo(0);
				mbo.setValue("persongroup", persongroup.getString("persongroup"), 2L);
			}
		}else {
			mbo.setValueNull("persongroup", 2L);
		}
		
	}
	
}
