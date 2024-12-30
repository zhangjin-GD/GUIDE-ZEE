package guide.app.common;


import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldVirAppName extends MboValueAdapter {

	public FldVirAppName(MboValue mbovalue) {
		super(mbovalue);
	}

	public void initValue() throws MXException, RemoteException {
		super.initValue();
		
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote appSet = mbo.getMboSet("WFASSIGNAPP");
		MboSetRemote wfSet = mbo.getMboSet("WFINSTANCE");
		if(!appSet.isEmpty() && appSet.count()>0){		
			mbo.setValue("udappname", appSet.getMbo(0).getString("description"), 11L);
		}
		if(!wfSet.isEmpty() && wfSet.count()>0){
			mbo.setValue("udoriginator", wfSet.getMbo(0).getString("originator.displayname"), 11L);
			mbo.setValue("uddept", wfSet.getMbo(0).getString("originator.uddept.description"), 11L);
		}
		
	}
	
}