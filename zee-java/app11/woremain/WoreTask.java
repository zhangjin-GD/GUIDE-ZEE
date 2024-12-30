package guide.app.woremain;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.workorder.UDWO;
import guide.app.workorder.WOBatch;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class WoreTask extends Mbo implements MboRemote {

	public WoreTask(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			String status = this.getString("status");
			String personId = this.getUserInfo().getPersonId();// 登录人
			// if (!CommonUtil.isAdmin(personId)) {
			if (this.toBeAdded()) {
				String[] attrs = { "DISPLAYNAME", "REMARK" };
				this.setFieldFlag(attrs, READONLY, true);
			} else {
				if ("E".equals(status)) {
					this.setFlag(READONLY, true);
				}
				// 未接受
				else if ("A".equals(status)) {
					String[] attrs = { "DISPLAYNAME", "REMARK" };
					this.setFieldFlag(attrs, READONLY, true);
				} // 接受计划/验收驳回 - 执行人/派发人可修改
				else if ("B".equals(status) || "D".equals(status)) {
					boolean isDisplay = false;// 执行人
					// 执行人,派发人 有权修改单据
					String dispatcherby = this.getString("dispatcherby");// 派发人
					String displayid = this.getString("displayid");// 执行人
					if (!displayid.isEmpty()) {
						String[] displayids = displayid.split(",");
						for (String displayby : displayids) {
							if (personId.equalsIgnoreCase(displayby)) {
								isDisplay = true;
							}
						}
					}
					String[] attrs = { "DISPLAYNAME", "ASSETNUM", "RANK", "WORKLEVEL", "PROPOSERBY", "WODESC",
							"RESULT" };
					if (personId.equalsIgnoreCase(dispatcherby) || isDisplay) {
						this.setFieldFlag(attrs, READONLY, true);
					} else {
						this.setFlag(READONLY, true);
					}
				}
				// 接受完成 /提出人即验收人 即班组可以修改
				if ("C".equals(status)) {
					String proposerby = this.getString("PROPOSERBY");
					MboSetRemote person1Set = this.getMboSet("$PERSON1", "PERSON", "personid ='" + proposerby + "'");
					MboSetRemote person2Set = this.getMboSet("$PERSON2", "PERSON", "personid ='" + personId + "'");
					String dept1 = "";
					String dept2 = "";
					if (!person1Set.isEmpty() && person1Set.count() > 0) {
						MboRemote person = person1Set.getMbo(0);
						if (!person.isNull("udofs")) {
							dept1 = person.getString("udofs");
						} else {
							dept1 = person.getString("uddept");
						}
					}
					if (!person2Set.isEmpty() && person2Set.count() > 0) {
						MboRemote person = person2Set.getMbo(0);
						if (!person.isNull("udofs")) {
							dept2 = person.getString("udofs");
						} else {
							dept2 = person.getString("uddept");
						}
					}
					String[] attrs = { "DISPLAYNAME", "ASSETNUM", "RANK", "WORKLEVEL", "PROPOSERBY", "WOJO1", "WOJO2",
							"WODESC", "REMARK" };
					if (!dept1.isEmpty() && !dept2.isEmpty() && (personId.equals(proposerby) || dept1.equals(dept2))) {
						this.setFieldFlag(attrs, READONLY, true);
					} else {
						this.setFlag(READONLY, true);
					}
				}
			}
			// }
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		int taskid = 10;
		String wodesc = "";
		if (parent != null) {
			if (parent instanceof UDWO) {
				this.setValue("refwo", parent.getString("wonum"), 11L);
				String worktype = parent.getString("worktype");
				if ("EM".equalsIgnoreCase(worktype)) {
					wodesc = parent.getString("udfailremedydesc");
				} else {
					wodesc = parent.getString("udfailanalysis");
				}
			}
			if (parent instanceof WOBatch) {
				this.setValue("wobatchnum", parent.getString("wobatchnum"), 11L);
			}
			if (parent instanceof WoreMain) {
				this.setValue("woremainnum", parent.getString("woremainnum"), 11L);
			}
			taskid = (int) getThisMboSet().max("taskid") + 10;
			this.setValue("assetnum", parent.getString("assetnum"), 11L);
		}
		String personId = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		this.setValue("createby", personId, 2L);// 创建人
		this.setValue("createtime", currentDate, 11L);// 创建时间
		this.setValue("taskid", taskid, 11L);
		this.setValue("status", "A", 11L);
		this.setValue("rank", "1", 11L);
		this.setValue("worklevel", "1", 11L);
		this.setValue("proposerby", personId, 2L);// 提出/检查人
		this.setValue("proposerdate", currentDate, 11L);// 提出/检查人时间
		this.setValue("wodesc", wodesc, 11L);
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
		setValue("changeby", getUserInfo().getPersonId(), 11L);
		setValue("changetime", MXServer.getMXServer().getDate(), 11L);
	}
}
