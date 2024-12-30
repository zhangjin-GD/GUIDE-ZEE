package guide.app.workorder;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import psdi.app.workorder.FldWOActualDate;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldWOActualDate extends FldWOActualDate {

	public UDFldWOActualDate(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void validate() throws MXException, RemoteException {
		double nh = 1000 * 60 * 60;
		MboRemote mbo = this.getMboValue().getMbo();
		Date sysDate = MXServer.getMXServer().getDate();// 系统时间
		Date targstartdate = mbo.getDate("targstartdate");// 故障开始时间
		Date actstart = mbo.getDate("actstart");// 维修开始时间
		Date actfinish = mbo.getDate("actfinish");// 维修结束时间
		String appName = mbo.getThisMboSet().getApp();
		if (appName != null && appName.equalsIgnoreCase("UDWOEM") && targstartdate != null && actstart != null
				&& targstartdate.after(actstart)) {
			throw new MXApplicationException("guide", "1012");// 提示，维修开始时间必须晚于故障开始时间！
		}

		if (appName != null && appName.equalsIgnoreCase("UDWOEM") && targstartdate != null && sysDate != null
				&& actfinish != null) {
			if (!mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
				double hour = sysDate.getTime() - targstartdate.getTime();
				double hours = hour / nh;
				if (hours > 12) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(targstartdate);
					calendar.add(Calendar.HOUR, 12);
					Date newTargstartDate = calendar.getTime();
					if (newTargstartDate.after(actfinish)) {
						throw new MXApplicationException("guide", "1047");// 提示，故障已超过12小时，维修结束时间只能填写12小时之后！
					}
				}
			}
		}
//		super.validate();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		double hours = 0, faildurHours = 0;
		double nh = 1000 * 60 * 60;
		String worktype = mbo.getString("worktype");
		boolean notshutdown = mbo.getBoolean("udnotshutdown");
		Date targstartdate = mbo.getDate("targstartdate");// 计划开始时间
		Date actstart = mbo.getDate("actstart");// 实际开始时间
		Date actfinish = mbo.getDate("actfinish");// 实际结束时间
		// 实际结束- 实际开始
		if (actstart != null && actfinish != null) {
			double hour = actfinish.getTime() - actstart.getTime();
			hours = hour / nh;
		}
		// 实际结束- 计划开始
		if (targstartdate != null && actfinish != null) {
			double faildur = actfinish.getTime() - targstartdate.getTime();
			faildurHours = faildur / nh;
		}
		mbo.setValue("estdur", hours, 11L);
		if ("EM".equalsIgnoreCase(worktype)) {
			mbo.setValue("udfaildur", faildurHours, 11L);
		} else {
			mbo.setValue("udfaildur", hours, 11L);
		}
		// 如果未停机设置实际故障时长为0
		if (notshutdown) {
			mbo.setValue("udfaildur", 0, 11L);
		}
	}
}
