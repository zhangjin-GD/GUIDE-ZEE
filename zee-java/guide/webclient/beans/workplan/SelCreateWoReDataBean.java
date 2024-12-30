package guide.webclient.beans.workplan;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import guide.app.common.CommonUtil;
import guide.app.workorder.UDWO;
import guide.app.workorder.UDWOSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelCreateWoReDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		Vector<MboRemote> vector = this.getSelection();
		String message = "提示，状态维护工单创建失败！";
		String flag = null;
		String wonumList = "";
		int addCount = 0;
		int waitAddCount = vector.size();

		for (int i = 0; i < vector.size(); i++) {
			MboRemote mr = (MboRemote) vector.elementAt(i);
			String oriwonum = "";
			String oriwonums = "";
			if (!mr.isNull("wonum")) {
				oriwonum = mr.getString("wonum");
				oriwonums = CommonUtil.autoKeyNum("WORKORDER", "UDORIWONUMS", oriwonum + "-", "", 2);
			}
			UDWOSet woSet = (UDWOSet) mbo.getMboSet("$WORKORDER", "WORKORDER", "1=2");
			// 设置第二天早上8点半
			Date sysdate = MXServer.getMXServer().getDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sysdate);
			calendar.add(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 8);
			calendar.set(Calendar.MINUTE, 30);
			calendar.set(Calendar.SECOND, 0);
			Date targstartdate = calendar.getTime();

			UDWO wo = (UDWO) woSet.add();
			wo.setValue("worktype", "CM", 2L);
			wo.setValue("assetnum", mr.getString("assetnum"), 2L);
			wo.setValue("description", mr.getString("asset.description") + "遗留问题", 11L);
			wo.setValue("targstartdate", targstartdate, 11L);
			wo.setValue("targcompdate", mr.getDate("planenddate"), 11L);
			wo.setValue("udoriwonum", oriwonum, 11L);
			wo.setValue("udoriwonums", oriwonums, 11L);
			String wonumnew = wo.getString("wonum");

			MboSetRemote woActivitySet = wo.getMboSet("UDGWOTASK");
			MboSetRemote woreTaskSet = mr.getMboSet("UDWORETASKNOTWO");
			if (!woreTaskSet.isEmpty() && woreTaskSet.count() > 0) {
				for (int j = 0; woreTaskSet.getMbo(j) != null; j++) {
					MboRemote woreTask = woreTaskSet.getMbo(j);
					MboRemote woActivity = woActivitySet.add();
					woActivity.setValue("linenum", woreTask.getInt("taskid"), 11L);
					woActivity.setValue("content", mr.getString("wodesc"), 11L);
					woActivity.setValue("mechname", mr.getString("wojo1"), 11L);
					woActivity.setValue("inspection", mr.getString("wojo2"), 11L);
					if (mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
						woActivity.setValue("result", "OK", 11L);
					} else {
						woActivity.setValue("result", "正常", 11L);
					}
					woreTask.setValue("wonum", wonumnew, 11L);
					woreTask.setValue("status", "ACTIVE", 2L);
				}
			}
			mr.setValue("status", "COMP", 11L);
			flag = wonumnew;
			if (flag != null && !flag.equalsIgnoreCase("")) {
				addCount++;
				wonumList += flag + ",";
			}
		}
		wonumList = wonumList.substring(0, wonumList.length() - 1);
		message = "提示，已选择" + waitAddCount + "条记录，成功创建" + addCount + "条，单号如下：" + wonumList + "！";
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", message, 1);
		return super.execute();
	}

}
