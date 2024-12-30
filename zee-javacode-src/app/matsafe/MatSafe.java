package guide.app.matsafe;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.common.UDMbo;
import guide.app.inventory.UDInvUseLine;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class MatSafe extends UDMbo implements MboRemote {

	public MatSafe(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();

		try {
			MboRemote parent = getOwner();
			// 领用-安全件信息
			if (parent != null && parent instanceof UDInvUseLine) {
				String[] attrs = { "ASSETNUM", "DESCRIPTION", "MATSAFETYPE", "PART", "MATSAFEDESC", "TEUSTD", "TEUWARN",
						"TEUACT", "UNITSTD", "UNITWARN", "UNITACT", "ACTIONSTD", "ACTIONWARN", "ACTIONACT", "RUNSTD",
						"RUNWARN", "RUNACT", "CALSTD", "CALWARN", "CALACT", "LOCKSTD", "LOCKWARN", "LOCKACT",
						"UPPERDATE" };
				int invuselineid1 = parent.getInt("invuselineid");
				int invuselineid2 = this.getInt("invuselineid");
				if (invuselineid1 != invuselineid2) {
					setFieldFlag(attrs, 7L, true);
				} else {
					setFieldFlag(attrs, 7L, false);
				}
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String appName = this.getThisMboSet().getApp();
		if (appName != null && !appName.isEmpty()) {
			if ("UDMATWL".equalsIgnoreCase(appName)) {
				this.setValue("matsafetype", "WL", 11L);
			}
			if ("UDMATTYRE".equalsIgnoreCase(appName)) {
				this.setValue("matsafetype", "TYRE", 11L);
			}
		}
		Date sysdate = MXServer.getMXServer().getDate();
		this.setValue("status", "RUN", 11L);
		this.setValue("lockwarn", 80, 11L);
		this.setValue("calwarn", 80, 11L);
		this.setValue("teuwarn", 80, 11L);
		this.setValue("unitwarn", 80, 11L);
		this.setValue("actionwarn", 80, 11L);
		this.setValue("runwarn", 80, 11L);
		this.setValue("upperdate", sysdate, 2L);
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();

		String matsafenum = this.getString("matsafenum");// 编号
		String assetnum = this.getString("assetnum");// 设备
		String matsafetype = this.getString("matsafetype");// 安全件类型
		String part = this.getString("part");// 机构
		String description = this.getString("description");// 安全件描述
		Date upperdate = MXServer.getMXServer().getDate();
		if (!this.isNull("upperdate")) {
			upperdate = this.getDate("upperdate");
		}
		// 如果设备 类型 描述 发生编号
		if (this.isModified("assetnum") || this.isModified("matsafetype") || this.isModified("part")
				|| this.isModified("description")) {
			// 上一个（旧）安全件 还原
			MboSetRemote parentSet = this.getMboSet("$UDPARENT", "UDMATSAFE",
					"parent='" + matsafenum + "' and status='INACTIVE'");
			if (!parentSet.isEmpty() && parentSet.count() > 0) {
				MboRemote parent = parentSet.getMbo(0);
				parent.setValueNull("parent", 11L);
				parent.setValueNull("lowerdate", 2L);
			}

			// 换上安全件变为换下
			String sql = "status = 'RUN' and assetnum = '" + assetnum + "' and matsafetype = '" + matsafetype
					+ "' and description = '" + description + "'";
			if (!this.isNull("part")) {
				sql += " and part = '" + part + "'";
			}
			MboSetRemote matsafeSet = this.getMboSet("$UDMATSAFE", "UDMATSAFE", sql);
			if (!matsafeSet.isEmpty() && matsafeSet.count() > 0) {
				MboRemote matsafe = matsafeSet.getMbo(0);
				if (!this.isNull("itemnum") && !matsafe.isNull("itemnum")) {
					String itemnum1 = matsafe.getString("itemnum");
					String itemnum2 = this.getString("itemnum");
					if (itemnum1.equalsIgnoreCase(itemnum2)) {
						if (!matsafe.isNull("lockstd")) {
							double lockstd = matsafe.getDouble("lockstd");
							this.setValue("lockstd", lockstd, 11L);
						}
						if (!matsafe.isNull("teustd")) {
							double teustd = matsafe.getDouble("teustd");
							this.setValue("teustd", teustd, 11L);
						}
						if (!matsafe.isNull("unitstd")) {
							double unitstd = matsafe.getDouble("unitstd");
							this.setValue("unitstd", unitstd, 11L);
						}
						if (!matsafe.isNull("actionstd")) {
							double actionstd = matsafe.getDouble("actionstd");
							this.setValue("actionstd", actionstd, 11L);
						}
						if (!matsafe.isNull("runstd")) {
							double runstd = matsafe.getDouble("runstd");
							this.setValue("runstd", runstd, 11L);
						}
						if (!matsafe.isNull("calstd")) {
							double calstd = matsafe.getDouble("calstd");
							this.setValue("calstd", calstd, 11L);
						}
					}
				}
				matsafe.setValue("parent", matsafenum, 11);
				matsafe.setValue("lowerdate", upperdate, 2L);// 换下时间
			}
		}
	}
}
