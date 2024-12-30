package guide.app.gpm;


import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class autoCreateWoCronTask extends SimpleCronTask {

	@Override
	public void cronAction() {
		try {
			String sqlWhere = getParamAsString("sqlWhere");
			MXServer server = MXServer.getMXServer();
			UserInfo userInfo = MXServer.getMXServer().getSystemUserInfo();
			MboSetRemote gpmMeterSet = server.getMboSet("UDGPMMETER", userInfo);
			gpmMeterSet.setWhere(sqlWhere);
			if (!gpmMeterSet.isEmpty() && gpmMeterSet.count() > 0) {
				int count = gpmMeterSet.count();
				MboRemote gpmMeter = null;
				MboSetRemote woSet = server.getMboSet("WORKORDER", userInfo);
				woSet.setWhere("1=2");
				MboRemote wo = null;
				String laborcode = null;
				for (int i = 0; count > i; i++) {
//				for (int i = 0; (gpmMeter = gpmMeterSet.getMbo(i)) != null; i++) {
					gpmMeter = gpmMeterSet.getMbo(0);
					wo = woSet.add();
					wo.setValue("description", gpmMeter.getString("description"), 11L);
					wo.setValue("assetnum", gpmMeter.getString("assetnum"), 2L);
					wo.setValue("udgpmnum", gpmMeter.getString("gpmnum"), 2L);
					laborcode = CommonUtil.getValue(gpmMeter, "UDGPM", "laborcode");
					if(laborcode != null && !laborcode.equalsIgnoreCase("")){
						wo.setValue("reportedby", laborcode, 11L);
						wo.setValue("changeby", laborcode, 11L);
					}
					woSet.save();
					gpmMeter.setValue("lastexetime", server.getDate(), 2L);
					gpmMeter.setValue("lastexevalue", gpmMeter.getDouble("currentvalue"), 2L);
					gpmMeterSet.save();
				}
				woSet.close();
			}
			gpmMeterSet.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}

	}

	
}
