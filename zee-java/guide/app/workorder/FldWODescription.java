package guide.app.workorder;


import guide.app.common.UDAuthCtrl;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWODescription extends MAXTableDomain {

	public FldWODescription(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ALNDOMAIN", "domainid='WOEMRPTDESC' and description =:" + thisAttr);
		String[] FromStr = { "description" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "domainid='WOEMRPTDESC'";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void validate() throws MXException, RemoteException {
//		super.validate();
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
    
    @Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String desc = this.getMboValue().getString();
		if (mbo.toBeAdded() && mbo.isNull("udwodesc")) {
			mbo.setValue("udwodesc", desc, 11L);
		}
	    if (mbo.toBeAdded() && mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
	        MboSetRemote gwoTaskSet = mbo.getMboSet("UDGWOTASK");
	        if (!gwoTaskSet.isEmpty() && gwoTaskSet.count() > 0) {
	          for (int i = 0; gwoTaskSet.getMbo(i) != null; i++) {
	            MboRemote gwoTask = gwoTaskSet.getMbo(i);
	            gwoTask.setValue("content", desc, 11L);
	          }
	        }
	      }
	}
}