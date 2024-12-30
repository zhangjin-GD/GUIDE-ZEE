package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldWplaborIslead extends MboValueAdapter{
	
	public FldWplaborIslead(MboValue mbv) {
		super(mbv);
	}
	
	@Override
	public void validate() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		boolean isLead = mbo.getBoolean("islead");
		String laborcode = mbo.getString("laborcode");
		MboSetRemote wplanlaborSet = mbo.getThisMboSet();
		if(!wplanlaborSet.isEmpty() && wplanlaborSet.count() > 0 && isLead && laborcode != null){
			MboRemote wplanlabor = null;
			for(int i=0;(wplanlabor=wplanlaborSet.getMbo(i))!=null;i++){
				if(isLead == wplanlabor.getBoolean("islead") && !laborcode.equalsIgnoreCase(wplanlabor.getString("laborcode")))
					throw new MXApplicationException("guide", "1010");
			}
		}
		super.validate();
	}

}
