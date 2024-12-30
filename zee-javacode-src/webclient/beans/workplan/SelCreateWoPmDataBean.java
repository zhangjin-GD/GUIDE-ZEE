package guide.webclient.beans.workplan;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import guide.app.common.CommonUtil;
import guide.app.gpm.UDGpm;
import psdi.mbo.MboRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelCreateWoPmDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		String flag = "";
		String wonumList = "";
		String planStartTimeStr = "";
		int addCount = 0;
		int waitAddCount = vector.size();
		for (int i = 0; i < vector.size(); i++) {
			MboRemote mr = (MboRemote) vector.elementAt(i);
			planStartTimeStr = getTimeStr(mr);

			Date targstartdate = null;
			Date targcompdate = null;
			try {
				targstartdate = CommonUtil.getDateFormat(planStartTimeStr, "yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			double zysc = mr.getDouble("udzysc");// 作业时长
			// 强转整数
			int zyscHous = (int) zysc;
			// 原数减去整数部分，为小数部分
			double doublePart = new BigDecimal(String.valueOf(new Double(zysc))).subtract(new BigDecimal(zyscHous))
					.doubleValue();
			double zyscDec = doublePart * 60;
			int zyscMinute = (int) zyscDec;
			if (targstartdate != null && !mr.isNull("udzysc")) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(targstartdate);
				calendar.add(Calendar.MINUTE, zyscMinute);
				calendar.add(Calendar.HOUR_OF_DAY, zyscHous);
				targcompdate = calendar.getTime();
			}

			flag = ((UDGpm) mr).addWoPm(targstartdate, targcompdate);

			if (flag != null && !flag.equalsIgnoreCase("")) {
				addCount++;
				wonumList += flag + ",";
			}
		}
		wonumList = wonumList.substring(0, wonumList.length() - 1);
		Object[] obj = { wonumList };
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "guide", "1134", obj);
		return super.execute();
	}

	private String getTimeStr(MboRemote gpm) {
		try {
			Date nextDate = CommonUtil.getCalDate(MXServer.getMXServer().getDate(), 1);
			String startDate = CommonUtil.getDateFormat(nextDate, "yyyy-MM-dd");
			String startTime = CommonUtil.getDateFormat(gpm.getDate("targstarttime"), "HH:mm:ss");
			String targstartdate = startDate + " " + startTime;
			return targstartdate;
		} catch (Exception e) {
			return null;
		}
	}

}
