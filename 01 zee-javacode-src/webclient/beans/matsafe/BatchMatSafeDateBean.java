package guide.webclient.beans.matsafe;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.matsafe.MatSafe;
import guide.app.matsafe.MatSafeSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class BatchMatSafeDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean appBean = this.app.getAppBean();
		DataBean table = app.getDataBean("matsafeall");
		MboRemote mbo = table.getMbo();
		if (mbo != null) {
			if (mbo.isNull("matsafetype") || mbo.isNull("part") || mbo.isNull("assettypecode") || mbo.isNull("assetnum")
					|| mbo.isNull("itemnum") || mbo.isNull("warn") || mbo.isNull("upperdate")) {
				throw new MXApplicationException("guide", "1184");
			}
			String matsafetype = mbo.getString("matsafetype");
			String assettypecode = mbo.getString("assettypecode");
			String assetnum = mbo.getString("assetnum");
			String part = mbo.getString("part");
			String itemnum = mbo.getString("itemnum");
			Date upperdate = mbo.getDate("upperdate");
			double partvalue = mbo.getDouble("partvalue");
			double lockvalue = mbo.getDouble("lockvalue");
			double lockact = mbo.getDouble("lockact");
			double warn = mbo.getDouble("warn");
			double teuinit = mbo.getDouble("teuinit");
			double unitinit = mbo.getDouble("unitinit");
			double actioninit = mbo.getDouble("actioninit");
			double runinit = mbo.getDouble("runinit");
			MboSetRemote lineSet = mbo.getMboSet("UDVMATSAFELINE");
			if (!lineSet.isEmpty() && lineSet.count() > 0) {
				MatSafeSet matsafeSet = (MatSafeSet) appBean.getMboSet();
				for (int i = 0; lineSet.getMbo(i) != null; i++) {
					MatSafe matsafe = (MatSafe) matsafeSet.add();
					MboRemote line = lineSet.getMbo(i);
					String matsafedesc = line.getString("matsafedesc");

					matsafe.setValue("matsafetype", matsafetype, 11L);
					// 设备
					matsafe.setValue("assettypecode", assettypecode, 11L);
					matsafe.setValue("assetnum", assetnum, 11L);
					// 机构
					matsafe.setValue("part", part, 11L);
					// 安全件
					matsafe.setValue("matsafedesc", matsafedesc, 2L);
					// 物资
					matsafe.setValue("itemnum", itemnum, 2L);
					// 预警百分比
					matsafe.setValue("lockwarn", warn, 11L);
					matsafe.setValue("teuwarn", warn, 11L);
					matsafe.setValue("unitwarn", warn, 11L);
					matsafe.setValue("actionwarn", warn, 11L);
					matsafe.setValue("runwarn", warn, 11L);
					matsafe.setValue("calwarn", warn, 11L);
					// 初始值
					matsafe.setValue("teuinit", teuinit, 2L);
					matsafe.setValue("unitinit", unitinit, 2L);
					matsafe.setValue("actioninit", actioninit, 2L);
					matsafe.setValue("runinit", runinit, 2L);
					// 更换日期
					matsafe.setValue("upperdate", upperdate, 2L);
					// 手动输入值
					matsafe.setValue("partvalue", partvalue, 2L);
					matsafe.setValue("lockvalue", lockvalue, 2L);
					matsafe.setValue("lockact", lockact, 2L);

					matsafeSet.save();
				}
			} else {
				throw new MXApplicationException("guide", "1185");
			}
		}
		this.app.getAppBean().save();
		return 1;
	}
}
