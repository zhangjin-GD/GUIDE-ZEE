package guide.app.inventory;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class InvMthlyCronTask extends SimpleCronTask {

	@Override
	public void cronAction() {
		//已作废，启用需重新编写
		try {
			MXServer server = MXServer.getMXServer();
			Date sysdate = server.getDate();
			String sysdateStr = CommonUtil.getCurrentDateFormat("yyyyMMdd");
			MboSetRemote deptSet = server.getMboSet("UDDEPT", server.getSystemUserInfo());
			deptSet.setWhere("type='COMPANY' and costcenter is not null");
			deptSet.reset();
			if (deptSet != null && !deptSet.isEmpty()) {
				MboSetRemote invmthlySet = null;
				for (int i = 0; deptSet.getMbo(i) != null; i++) {
					MboRemote dept = deptSet.getMbo(i);
					String company = dept.getString("company");
					String sql = "udcompany='" + company + "' and to_char(mthlydate,'YYYY-MM-DD')='" + sysdateStr + "'";
					invmthlySet = server.getMboSet("UDINVMTHLY", server.getSystemUserInfo());
					invmthlySet.setWhere(sql);
					if (invmthlySet != null && invmthlySet.isEmpty()) {
						InvMthly mbo = (InvMthly) invmthlySet.add();
						mbo.setValue("description", company+sysdateStr + " 物资月台账", 11L);
						mbo.setValue("udcompany", company, 2L);
						mbo.setValue("mthlydate", sysdate, 11L);
						String invmthlynum = mbo.getString("invmthlynum");
						// 新增行信息
						mbo.invMonthly(invmthlynum);
						invmthlySet.save();
					}
				}
				invmthlySet.close();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}

	}

}
