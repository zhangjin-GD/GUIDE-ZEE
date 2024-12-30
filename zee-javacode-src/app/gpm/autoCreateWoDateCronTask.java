package guide.app.gpm;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class autoCreateWoDateCronTask extends SimpleCronTask {

	@Override
	public void cronAction() {
		try {
			String sqlWhere = getParamAsString("sqlWhere");
			MXServer server = MXServer.getMXServer();
			UserInfo userInfo = server.getSystemUserInfo();
			MboSetRemote gpmSet = server.getMboSet("UDGPM", userInfo);
			gpmSet.setWhere(sqlWhere);
			gpmSet.setOrderBy("assetnum,gpmnum");
			gpmSet.reset();
			if (!gpmSet.isEmpty() && gpmSet.count() > 0) {
				Date currentDate = server.getDate();// 系统日期
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				MboSetRemote woSet = server.getMboSet("WORKORDER", userInfo);
				woSet.setWhere("1=2");
				for (int i = 0; gpmSet.getMbo(i) != null; i++) {
					MboRemote gpm = gpmSet.getMbo(i);
					Date nextdate = gpm.getDate("nextdate");// 下次执行日期
					if (nextdate != null && currentDate != null) {
						boolean usetargetdate = gpm.getBoolean("usetargetdate");
						boolean isstopasset = gpm.getBoolean("isstopasset");
						boolean isadd = false;
						Date planstarttime = null;
						if (isstopasset) {
							// 是否在停机申请计划中
							MboSetRemote assetStopSet = gpm.getMboSet("ASSETSTOP");
							if (!assetStopSet.isEmpty() && assetStopSet.count() > 0) {
								MboRemote assetStop = assetStopSet.getMbo(0);
								planstarttime = assetStop.getDate("planstarttime");
								currentDate = planstarttime;
								isadd = true;
							}
						} else {
							// 格式化
							String nextDateStr = format.format(nextdate);
							String currentDateStr = format.format(currentDate);

							Date nextDateD = format.parse(nextDateStr);
							Date currentDateD = format.parse(currentDateStr);

							long nextDateT = nextDateD.getTime();
							long currentDateT = currentDateD.getTime();
							if (nextDateT <= currentDateT) {
								isadd = true;
							}
						}
						if (isadd) {
							MboRemote wo = woSet.add();
							wo.setValue("description", gpm.getString("description"), 11L);
							wo.setValue("assetnum", gpm.getString("assetnum"), 2L);
							wo.setValue("udgpmnum", gpm.getString("gpmnum"), 2L);

							if (planstarttime != null) {
								wo.setValue("targstartdate", planstarttime, 11L);
							}

							if (!gpm.isNull("laborcode")) {
								wo.setValue("reportedby", gpm.getString("laborcode"), 11L);
								wo.setValue("changeby", gpm.getString("laborcode"), 11L);
							}
							woSet.save();
							if (usetargetdate) {
								gpm.setValue("laststartdate", currentDate, 2L);
							} else {
								gpm.setValue("laststartdate", currentDate, 2L);
								gpm.setValueNull("nextdate");
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
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
