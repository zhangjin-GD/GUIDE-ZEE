package guide.app.workorder;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import psdi.app.workorder.FldWOTargetDate;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldWOTargetDate extends FldWOTargetDate {

	public UDFldWOTargetDate(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void validate() throws MXException, RemoteException {
//		double nh = 1000 * 60 * 60;
		MboRemote mbo = this.getMboValue().getMbo();
		String woanalysis = mbo.getString("udwoanalysis");
//		Date reportdate = mbo.getDate("reportdate");// 报修日期
//		Date targstartdate = mbo.getDate("targstartdate");// 计划开始时间
		Date targcompdate = mbo.getDate("targcompdate");// 计划结束时间
		Date actfinish = mbo.getDate("actfinish");// 实际结束时间
		String appName = mbo.getThisMboSet().getApp();
		if (appName != null && (appName.equalsIgnoreCase("UDWOEM") || appName.equalsIgnoreCase("UDWOEMRPT"))) {
			// 故障报修日期-1小时 < 故障开始时间 < 故障报修日期
//			if (reportdate != null && targstartdate != null) {
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(reportdate);
//				calendar.add(Calendar.HOUR_OF_DAY, -1);
//				Date reportdate2 = calendar.getTime();
//				if (reportdate2.after(targstartdate) || targstartdate.after(reportdate)) {
//					throw new MXApplicationException("guide", "1095");
//				}
//			}
			if ((targcompdate != null && actfinish != null && actfinish.after(targcompdate))
					&& !"4MISREPORT".equalsIgnoreCase(woanalysis)) {
				throw new MXApplicationException("guide", "1011");
			}
		}
		// 状态 预防性维护 辅助工作 创建时间-12 >= 计划开始时间
//		if (appName != null && (appName.equalsIgnoreCase("UDWOCM") || appName.equalsIgnoreCase("UDWOPM")
//				|| appName.equalsIgnoreCase("UDWOSW"))) {
//			if (reportdate != null && targstartdate != null) {
//				double hours = (reportdate.getTime() - targstartdate.getTime()) / nh;
//				if (hours > 12 || hours < 0) {
//					throw new MXApplicationException("guide", "1096");
//				}
//			}
//			if (targstartdate != null && targcompdate != null && targstartdate.after(targcompdate)) {
//				throw new MXApplicationException("guide", "1096");
//			}
//		}
		super.validate();
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
		Date targcompdate = mbo.getDate("targcompdate");// 计划结束时间
		Date actfinish = mbo.getDate("actfinish");// 实际结束时间
		// 计划结束-计划开始
		if (targstartdate != null && targcompdate != null) {
			double hour = targcompdate.getTime() - targstartdate.getTime();
			hours = hour / nh;
		}
		// 实际结束-计划开始
		if (targstartdate != null && actfinish != null) {
			double faildur = actfinish.getTime() - targstartdate.getTime();
			faildurHours = faildur / nh;
		}
		mbo.setValue("remdur", hours, 11L);
		if ("EM".equalsIgnoreCase(worktype)) {
			mbo.setValue("udfaildur", faildurHours, 11L);
		}
		// 如果未停机设置实际故障时长为0
		if (notshutdown) {
			mbo.setValue("udfaildur", 0, 11L);
		}
	}
}
