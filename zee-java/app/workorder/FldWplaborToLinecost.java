package guide.app.workorder;


import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;


public class FldWplaborToLinecost extends MboValueAdapter
{

    public FldWplaborToLinecost(MboValue mbv)
        throws MXException
    {
        super(mbv);
    }
    
    public void action() throws RemoteException, MXException{
    	super.action();
    	
    	MboRemote mbo = getMboValue().getMbo();
    	double workTime = mbo.getDouble("worktime");
    	double unitCost = mbo.getDouble("unitcost");
    	if(workTime >= 0 && unitCost >= 0){
    		mbo.setValue("linecost", workTime*unitCost, 2L);
    	}

    }
    
    
}