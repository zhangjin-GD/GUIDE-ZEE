package guide.app.workorder;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import guide.app.common.CommonUtil;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldGpmNum extends MAXTableDomain {

	public FldGpmNum(MboValue mbv) throws MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDGPM", "gpmnum=:" + thisAttr);
		String[] FromStr = { "gpmnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		MboRemote mbo = this.getMboValue().getMbo();
		String sql = "status = 'ACTIVE' and udcompany = :udcompany and uddept = :uddept and assetnum = :assetnum and gjpnum in (select gjpnum from udgjobplan where worktype = :worktype and assettype = :udassettypecode)";
		if (!mbo.isNull("udofs")) {
			sql += " and udofs =:udofs";
		}
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		Date date = MXServer.getMXServer().getDate();
		String wonum = mbo.getString("wonum");
		MboSetRemote gpmSet = mbo.getMboSet("UDGPM");
		if (gpmSet != null && !gpmSet.isEmpty()) {
			MboRemote gpm = gpmSet.getMbo(0);

			String startDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");
			String startTime = CommonUtil.getDateFormat(gpm.getDate("targstarttime"), "HH:mm:ss");
			int pmcounter = gpm.getInt("pmcounter") + 1;// 下一个序号
			double zysc = gpm.getDouble("udzysc");// 作业时长
			// 设置工单状态
			if (!gpm.isNull("wostatus")) {
				String status = gpm.getString("WOSTATUS");
				mbo.setValue("status", status, 2L);
			}
			// 维修类型 内修/外修
			mbo.setValue("udrepairtype", gpm.getString("udrepairtype"), 11L);
			try {
				String start = startDate + " " + startTime;
				Date targstartdate = CommonUtil.getDateFormat(start, "yyyy-MM-dd HH:mm:ss");
				// 计划开始日期
				mbo.setValue("targstartdate", targstartdate, 2L);
				// 强转整数
				int zyscHous = (int) zysc;
				// 原数减去整数部分，为小数部分
				double doublePart = new BigDecimal(String.valueOf(new Double(zysc))).subtract(new BigDecimal(zyscHous))
						.doubleValue();
				double zyscDec = doublePart * 60;
				int zyscMinute = (int) zyscDec;
				if (targstartdate != null && !gpm.isNull("udzysc")) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(targstartdate);
					calendar.add(Calendar.MINUTE, zyscMinute);
					calendar.add(Calendar.HOUR_OF_DAY, zyscHous);
					// 计划结束日期 开始日期+时长
					mbo.setValue("targcompdate", calendar.getTime(), 2L);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

			// 作业人员
			MboSetRemote gjobLaborSet = gpm.getMboSet("UDGJOBLABOR");
			if (!gjobLaborSet.isEmpty() && gjobLaborSet.count() > 0) {
				MboSetRemote wplaborSet = mbo.getMboSet("UDWPLABOR");
				if (wplaborSet.isEmpty()) {
					for (int i = 0; gjobLaborSet.getMbo(i) != null; i++) {
						MboRemote gjobLabor = gjobLaborSet.getMbo(i);
						MboRemote wplabor = wplaborSet.add();
						wplabor.setValue("wonum", wonum, 11L);
						wplabor.setValue("laborcode", gjobLabor.getString("laborcode"), 11L);
						wplabor.setValue("islead", gjobLabor.getBoolean("islead"), 11L);
					}
				}
			}

			// 获取下一个JP编号
			MboSetRemote gpmSeqSet = gpm.getMboSet("UDGPMSEQUENCE");
			int gpmSeqCount = gpmSeqSet.count();
			int linenumNext = 1;
			if (pmcounter <= gpmSeqCount) {
				linenumNext = pmcounter;
			}

			if (!gpmSeqSet.isEmpty() && gpmSeqSet.count() > 0) {
				String gjpnum = "";
				for (int i = 0; gpmSeqSet.getMbo(i) != null; i++) {
					MboRemote gpmSeq = gpmSeqSet.getMbo(i);
					int linenum = gpmSeq.getInt("linenum");
					if (linenumNext == linenum) {
						gjpnum = gpmSeq.getString("gjpnum");
					}
				}
				mbo.setValue("udgjpnum", gjpnum, 2L);
			}

			// 更新PM
			gpm.setValue("laststartdate", date, 2L);
			gpm.setValue("pmcounter", linenumNext, 11L);
			MboSetRemote gpmMeterSet = gpm.getMboSet("UDGPMMETER");
			if (!gpmMeterSet.isEmpty() && gpmMeterSet.count() > 0) {
				for (int i = 0; gpmMeterSet.getMbo(i) != null; i++) {
					MboRemote gpmMeter = gpmMeterSet.getMbo(i);
					double valuelast = 0;
					MboSetRemote measurePointSet = gpmMeter.getMboSet("UDMEASUREPOINT");
					if (!measurePointSet.isEmpty() && measurePointSet.count() > 0) {
						valuelast = measurePointSet.getMbo(0).getDouble("valuelast");
					}
					gpmMeter.setValue("lastexevalue", valuelast, 2L);
				}
			}
		}
	}

}
