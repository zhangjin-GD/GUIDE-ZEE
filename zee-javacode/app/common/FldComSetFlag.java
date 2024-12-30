package guide.app.common;


import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldComSetFlag extends MboValueAdapter
{

    public FldComSetFlag(MboValue mbv)
        throws MXException
    {
        super(mbv);
    }
    
	public void init() throws MXException, RemoteException {
		super.init();
		
		MboRemote mbo = getMboValue().getMbo();
		
		try {
			if(mbo.toBeAdded())
				return;
			
			MboRemote appmbo = mbo.getOwner();
			if(appmbo == null)
				appmbo = mbo;
			
			MboSetRemote authctrlSet = appmbo.getMboSet("$UDAUTHCTRL", "UDAUTHCTRL", "objectname='"+mbo.getName()+"' and app='"+appmbo.getThisMboSet().getApp()+"'");
			if(!authctrlSet.isEmpty() && authctrlSet.count() > 0)
				((UDAuthCtrl)authctrlSet.getMbo(0)).authctrl(appmbo,mbo);
			
		} catch (RemoteException e) {
		}

	}
    
    
}