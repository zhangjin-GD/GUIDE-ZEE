package guide.app.asset;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldAssetStopLinePlanstarttime extends MboValueAdapter {

	public FldAssetStopLinePlanstarttime(MboValue mbv) {
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

		Date planstarttime = mbo.getDate("planstarttime");// 计划开始时间
		Date planendtime = mbo.getDate("planendtime");// 计划结束时间
		// 计划结束时间不能小于计划开始实际
		if (planstarttime != null && planendtime != null) {
			long planStarttimeT = planstarttime.getTime();
			long planEndTimeT = planendtime.getTime();
			if (planEndTimeT < planStarttimeT) {
				throw new MXApplicationException("guide", "1136");// 提示，计划结束时间不能小于计划开始时间
			}
		}

		// 碎片保养
		if (type != null && type.equalsIgnoreCase("assetpm")) {
			String planStartTimeStr = CommonUtil.getDateFormat(mbo.getDate("planstarttime"), "yyyy-MM-dd");
			String curDateStr = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");
			if (planStartTimeStr != null && curDateStr != null && !planStartTimeStr.equalsIgnoreCase(curDateStr)) {
				throw new MXApplicationException("guide", "1078");
			}
		}
		// 停机申请
		if (type != null && type.equalsIgnoreCase("assetstop")) {
			if (planstarttime != null) {
				long planStarttimeT = planstarttime.getTime();
				long sysTime = MXServer.getMXServer().getDate().getTime();
				if (planStarttimeT < sysTime) {
					throw new MXApplicationException("guide", "1137");// 提示，计划开始时间不能小于当前时间！
				}
			}
		}
	}

}
