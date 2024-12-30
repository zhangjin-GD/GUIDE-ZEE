package guide.app.security;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class Permit extends UDMbo implements MboRemote {

	public Permit(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String appName = this.getThisMboSet().getApp();
		this.setValue("usetime", 0, 11L);
		if (appName != null && !appName.isEmpty()) {
			String appType = appName.replaceAll("UD", ""); // 替换UD
			this.setValue("apptype", appType, 11L);
			this.getMboValue("permitnum").autoKey();
			insertPermitIndex(appName);
			insertPermitTask(appName);
		}
	}

	/**
	 * 创建安全分析
	 * 
	 * @param appName
	 * @throws RemoteException
	 * @throws MXException
	 */
	private void insertPermitIndex(String appName) throws RemoteException, MXException {
		MboSetRemote tempSet = this.getMboSet("$UDPERMITTEMP", "UDPERMITTEMP",
				"TEMPTYPE='ANALYSIS' AND APPTYPE='" + appName + "'");
		if (!tempSet.isEmpty() && tempSet.count() > 0) {
			MboRemote temp = tempSet.getMbo(0);
			MboSetRemote tempLineSet = temp.getMboSet("UDPERMITTEMPLINE");
			if (!tempLineSet.isEmpty() && tempLineSet.count() > 0) {
				MboSetRemote indexSet = this.getMboSet("UDPERMITINDEX");
				if (!indexSet.isEmpty() && indexSet.count() > 0) {
					indexSet.deleteAll();
				}
				for (int i = 0; tempLineSet.getMbo(i) != null; i++) {
					MboRemote tempLine = tempLineSet.getMbo(i);
					int linenum = tempLine.getInt("linenum");
					String project = tempLine.getString("project");
					String standard = tempLine.getString("standard");
					String position = tempLine.getString("position");
					MboRemote index = indexSet.addAtEnd();
					index.setValue("linenum", linenum, 11L);
					index.setValue("project", project, 11L);
					index.setValue("standard", standard, 11L);
					index.setValue("POSITION", position, 11L);
					index.setValue("permitnum", this.getString("permitnum"), 11L);
				}
			}
		}
	}

	/**
	 * 创建安全措施
	 * 
	 * @param appName
	 * @throws RemoteException
	 * @throws MXException
	 */
	private void insertPermitTask(String appName) throws RemoteException, MXException {
		MboSetRemote tempSet = this.getMboSet("$UDPERMITTEMP", "UDPERMITTEMP",
				"TEMPTYPE='REMEDY' AND APPTYPE='" + appName + "'");
		if (!tempSet.isEmpty() && tempSet.count() > 0) {
			MboRemote temp = tempSet.getMbo(0);
			MboSetRemote tempLineSet = temp.getMboSet("UDPERMITTEMPLINE");
			if (!tempLineSet.isEmpty() && tempLineSet.count() > 0) {
				MboSetRemote taskSet = this.getMboSet("UDPERMITTASK");
				if (!taskSet.isEmpty() && taskSet.count() > 0) {
					taskSet.deleteAll();
				}
				for (int i = 0; tempLineSet.getMbo(i) != null; i++) {
					MboRemote tempLine = tempLineSet.getMbo(i);
					int linenum = tempLine.getInt("linenum");
					String description = tempLine.getString("description");
					MboRemote task = taskSet.addAtEnd();
					task.setValue("linenum", linenum, 11L);
					task.setValue("description", description, 11L);
					task.setValue("permitnum", this.getString("permitnum"), 11L);
				}
			}
		}
	}
}
