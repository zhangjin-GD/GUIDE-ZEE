package guide.app.asset;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldAssetStopLineActEndTime extends MboValueAdapter {

	public FldAssetStopLineActEndTime(MboValue mbv) {
		super(mbv);
	}

	public void init() throws RemoteException, MXException {
		super.init();
	}

	@Override
	public void validate() throws MXException, RemoteException {
		super.validate();
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String type = "";
		if (owner != null) {
			type = owner.getString("udapptype");
		} else {
			MboSetRemote assetStopSet = mbo.getMboSet("UDASSETSTOP");
			if (!assetStopSet.isEmpty() && assetStopSet.count() > 0) {
				MboRemote assetStop = assetStopSet.getMbo(0);
				type = assetStop.getString("udapptype");
			}
		}

		Date actstarttime = mbo.getDate("actstarttime");// 实际开始时间
		Date actendtime = mbo.getDate("actendtime");// 实际结束时间
		// 计划结束时间不能小于计划开始实际
		if (actstarttime != null && actendtime != null) {
			long actstarttimeT = actstarttime.getTime();
			long actendTimeT = actendtime.getTime();
			if (actendTimeT < actstarttimeT) {
				throw new MXApplicationException("guide", "1135");
			}
		}

		// 停机申请
		if (type != null && type.equalsIgnoreCase("assetstop")) {
			if (actendtime != null) {
				long actEndTimeT = actendtime.getTime();
				long sysTime = MXServer.getMXServer().getDate().getTime();
				if (actEndTimeT < sysTime) {
					throw new MXApplicationException("guide", "1142");// 提示，实际结束时间不能小于当前时间！
				}
			}
		}
	}

}
