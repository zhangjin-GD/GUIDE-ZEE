package guide.app.matsafe;


import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldForColour extends MboValueAdapter {

	public FldForColour(MboValue mbovalue) {
		super(mbovalue);
	}

	public void action() throws MXException, RemoteException {
		super.action();
		
		MboRemote mbo = this.getMboValue().getMbo();
		String status = mbo.getString("status");
		if(status != null && !status.equalsIgnoreCase("INACTIVE")){
			double actionPer = 0;
			double calPer = 0;
			double runPer = 0;
			
			double actionStd = mbo.getDouble("actionstd");
			double actionAct = mbo.getDouble("actionact");
			double calStd = mbo.getDouble("calstd");
			double calAct = mbo.getDouble("calact");
			double runStd = mbo.getDouble("runstd");
			double runAct = mbo.getDouble("runact");
			double cautionPer = mbo.getDouble("cautionper");
			
			if(actionStd != 0)
				actionPer = actionAct/actionStd*100;
			if(calStd != 0)
				calPer = calAct/calStd*100;
			if(runStd != 0)
				runPer = runAct/runStd*100;
	
			if(actionPer <= cautionPer && calPer <= cautionPer && runPer <= cautionPer)
				mbo.setValue("cautionflag", "A", 11L);
			
			if(actionPer > cautionPer || calPer > cautionPer || runPer > cautionPer)
				mbo.setValue("cautionflag", "B", 11L);//黄色
			
			if(actionAct > actionStd || calAct > calStd || runAct > runStd)
				mbo.setValue("cautionflag", "C", 11L);//红色
			
	//		System.out.println("\n-----"+actionPer+"-----"+calPer+"-----"+runPer+"-----"+cautionPer+"-----"+mbo.getString("cautionflag"));
		}
	}
	
}