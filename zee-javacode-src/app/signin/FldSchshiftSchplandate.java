package guide.app.signin;


import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;


public class FldSchshiftSchplandate extends MboValueAdapter
{

	public FldSchshiftSchplandate(MboValue mbv) {
		super(mbv);
	}

	public void validate() throws RemoteException, MXException {
		super.validate();
		
	}
	
    public void action() throws RemoteException, MXException{
    	super.action();
    	
    	MboRemote mbo = getMboValue().getMbo();
    	String description = mbo.getString("description");
    	Date schPlanDate = mbo.getDate("schplandate");
		MboSetRemote signShiftSet = mbo.getMboSet("UDSIGNSHIFT");
		if (signShiftSet != null && !signShiftSet.isEmpty() && schPlanDate != null) {
			MboRemote signShift = signShiftSet.getMbo(0);
			if(description != null && !description.equalsIgnoreCase("")){
				mbo.setValue("description", description, 11L);
			}
			mbo.setValue("starttime", CommonUtil.getDateFormat(schPlanDate, "yyyy-MM-dd")+" "+signShift.getString("starttime"), 11L);
			if(signShift.getDate("starttime").after(signShift.getDate("endtime"))){
				schPlanDate = CommonUtil.getCalDate(schPlanDate, 1);
			}
			mbo.setValue("endtime", CommonUtil.getDateFormat(schPlanDate, "yyyy-MM-dd")+" "+signShift.getString("endtime"), 11L);
		}
		
    }
    
    
}