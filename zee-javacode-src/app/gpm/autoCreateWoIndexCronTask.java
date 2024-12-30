package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class autoCreateWoIndexCronTask extends SimpleCronTask {

	@Override
	public void cronAction() {
		try {
			System.out.println("--开始------------>");
			String sqlWhere = getParamAsString("sqlWhere");
			MXServer server = MXServer.getMXServer();
			UserInfo userInfo = server.getSystemUserInfo();
			MboSetRemote gpmSet = server.getMboSet("UDGPM", userInfo);
			gpmSet.setWhere(sqlWhere);
			gpmSet.setOrderBy("assetnum,serialnum");
			gpmSet.reset();
			if (!gpmSet.isEmpty() && gpmSet.count() > 0) {
				MboSetRemote woSet = server.getMboSet("WORKORDER", userInfo);
				woSet.setWhere("1=2");
				for (int i = 0; gpmSet.getMbo(i) != null; i++) {
					MboRemote gpm = gpmSet.getMbo(i);
					double actioncur = gpm.getDouble("actioncur");// 当前值
					double actionnext = gpm.getDouble("actionnext");// 下次执行
					double serialnum = gpm.getDouble("serialnum");
					double nextnum = gpm.getDouble("nextnum");
					if (actioncur >= actionnext && serialnum == nextnum) {
						MboRemote wo = woSet.add();
						wo.setValue("description", gpm.getString("description"), 11L);
						wo.setValue("assetnum", gpm.getString("assetnum"), 2L);
						wo.setValue("udgpmnum", gpm.getString("gpmnum"), 2L);
						if (!gpm.isNull("laborcode")) {
							wo.setValue("reportedby", gpm.getString("laborcode"), 11L);
							wo.setValue("changeby", gpm.getString("laborcode"), 11L);
						}
						woSet.save();
						MboSetRemote gpmTypeSet = gpm.getMboSet("UDGPMTYPE");
						MboSetRemote gpmTypeNextSet = gpm.getMboSet("UDGPMTYPENEXT");
						double serialnumMax = gpmTypeSet.max("serialnum");
						nextnum = gpmTypeNextSet.min("serialnum");
						if (!gpmTypeSet.isEmpty() && gpmTypeSet.count() > 0) {
							for (int j = 0; gpmTypeSet.getMbo(j) != null; j++) {
								MboRemote gpmMeterType = gpmTypeSet.getMbo(j);
								gpmMeterType.setValue("actionlast", actioncur, 2L);
								if (nextnum > serialnumMax || nextnum == 0) {
									nextnum = 1;
								}
								gpmMeterType.setValue("nextnum", nextnum, 11L);
							}
						}
					}
					gpm.getThisMboSet().save();
				}
			}
			gpmSet.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
		System.out.println("--结束------------>");
	}

}
