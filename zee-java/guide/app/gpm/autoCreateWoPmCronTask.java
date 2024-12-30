package guide.app.gpm;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import guide.app.workorder.UDWO;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;
import psdi.workflow.WorkFlowServiceRemote;

public class autoCreateWoPmCronTask extends SimpleCronTask {

	@Override
	public void cronAction() {
		try {
			System.out.println("---开始创建PM工单---");
			Set<String> hashSet = getPMActive();
			System.out.println("---开始addPmWo---");
			addPmWo(hashSet);
			System.out.println("---结束创建PM工单---");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private Set<String> getPMActive() throws RemoteException, MXException, ParseException {
		String sqlWhere = getParamAsString("sqlWhere");
		MXServer server = MXServer.getMXServer();
		UserInfo userInfo = server.getSystemUserInfo();
		MboSetRemote gpmSet = server.getMboSet("UDGPM", userInfo);
		gpmSet.setWhere(sqlWhere);
		gpmSet.setOrderBy("assetnum,gpmnum");
		gpmSet.reset();
		Set<String> hashSet = new LinkedHashSet<String>();
		System.out.println("\n--721----gpmSet.count()----"+gpmSet.count());
		if (!gpmSet.isEmpty() && gpmSet.count() > 0) {
			Date sysDate = MXServer.getMXServer().getDate();// 当前日期
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//			for (int i = 0; gpmSet.getMbo(i) != null; i++) {
			System.out.println("\n--721----gpmSet.count()111111----");
			for (int i = 0; i < gpmSet.count(); i++) {
				System.out.println("\n--721----gpmSet.count()22222----");
				MboRemote gpm = gpmSet.getMbo(i);
				String gpmnum = gpm.getString("gpmnum");
				Date nextdate = gpm.getDate("nextdate");// 下次执行日期
				if (nextdate != null && sysDate != null) {
					boolean isstopasset = gpm.getBoolean("isstopasset");// 是否关联停机申请
					boolean isadd = false;
					if (isstopasset) {
						// 是否在停机申请计划中
						MboSetRemote assetStopSet = gpm.getMboSet("ASSETSTOP");
						if (!assetStopSet.isEmpty() && assetStopSet.count() > 0) {
							isadd = true;
						}
					} else {
						// 格式化
						String nextDateStr = format.format(nextdate);
						String sysDateStr = format.format(sysDate);

						Date nextDateD = format.parse(nextDateStr);
						Date sysDateD = format.parse(sysDateStr);

						long nextDateT = nextDateD.getTime();
						long sysDateT = sysDateD.getTime();
						if (sysDateT >= nextDateT) {
							isadd = true;
						}
					}
					if (isadd) {
						hashSet.add(gpmnum);
					}
				}
				MboSetRemote gpmMeterSet = gpm.getMboSet("UDGPMMETER");
				if (!gpmMeterSet.isEmpty() && gpmMeterSet.count() > 0) {
					for (int j = 0; gpmMeterSet.getMbo(j) != null; j++) {
						MboRemote gpmMeter = gpmMeterSet.getMbo(j);
						double nextvalue = gpmMeter.getDouble("nextvalue");
						double valuelast = 0;
						MboSetRemote measurePointSet = gpmMeter.getMboSet("UDMEASUREPOINT");
						if (!measurePointSet.isEmpty() && measurePointSet.count() > 0) {
							MboRemote measurePoint = measurePointSet.getMbo(0);
							valuelast = measurePoint.getDouble("valuelast");
						}
						if (valuelast >= nextvalue) {
							hashSet.add(gpmnum);
						}
					}
				}
			}
		}
		gpmSet.close();
		return hashSet;
	}

	private void addPmWo(Set<String> hashSet) throws RemoteException, MXException {
		for (String value : hashSet) {
			UDGpmSet gpmSet = (UDGpmSet) MXServer.getMXServer().getMboSet("UDGPM", getRunasUserInfo());
			gpmSet.setWhere("gpmnum='" + value + "' and status ='ACTIVE' and autowo=1");
			gpmSet.reset();
			UDGpm gpm = (UDGpm) gpmSet.getMbo(0);
			boolean autowf = gpm.getBoolean("autowf");
			String wonum = gpm.addWoPm(null, null);
			gpmSet.close();
			// 是否自动发工作流
			if (autowf) {
				// 获取工单信息
				MboSetRemote woSet = MXServer.getMXServer().getMboSet("workorder", getRunasUserInfo());
				woSet.setWhere("wonum = '" + wonum + "'");
				woSet.reset();
				if (!woSet.isEmpty() && woSet.count() > 0) {
					UDWO wo = (UDWO) woSet.getMbo(0);
					MboSetRemote woLaborSet = wo.getMboSet("UDWPLABOR");
					// 判断是否有员工
					if (!woLaborSet.isEmpty() && woLaborSet.count() > 0) {
						// 自动发送工作流
						WorkFlowServiceRemote wfServiceRemote = (WorkFlowServiceRemote) MXServer.getMXServer()
								.lookup("WORKFLOW");
						boolean isEnable = wfServiceRemote.isActiveProcess("UDWOPM", "WORKORDER", getRunasUserInfo());
						if (isEnable && wfServiceRemote.getActiveInstances(wo).isEmpty()) {
							wfServiceRemote.initiateWorkflow("UDWOPM", wo);
						}
					}
				}
				woSet.close();
			}
			
			/**
			 * ZEE
			 * 2023-07-21 12:33:33
			 */
			String udcompany = gpm.getString("udcompany");
			if (autowf && udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
				// 获取工单信息
				MboSetRemote woSet = MXServer.getMXServer().getMboSet("workorder", getRunasUserInfo());
				woSet.setWhere("wonum = '" + wonum + "'");
				woSet.reset();
				if (!woSet.isEmpty() && woSet.count() > 0) {
					UDWO wo = (UDWO) woSet.getMbo(0);
					// 自动发送工作流
					WorkFlowServiceRemote wfServiceRemote = (WorkFlowServiceRemote) MXServer.getMXServer().lookup("WORKFLOW");
					boolean isEnable = wfServiceRemote.isActiveProcess("UDWOZEE", "WORKORDER", getRunasUserInfo());
					if (isEnable && wfServiceRemote.getActiveInstances(wo).isEmpty()) {
						wfServiceRemote.initiateWorkflow("UDWOZEE", wo);
					}
				}
				woSet.close();
			}
		}
	}
}
