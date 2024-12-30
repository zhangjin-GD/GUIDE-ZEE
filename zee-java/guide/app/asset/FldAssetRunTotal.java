package guide.app.asset;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.common.CommonUtil;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldAssetRunTotal extends MboValueAdapter {

	public FldAssetRunTotal(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();

		double workHour = 0.00d;
		double boxUnit = 0.00d;
		double boxTeu = 0.00d;
		double electricKwh = 0.00d;
		double oilL = 0.00d;
		double worklock = 0.00d;
		double control = 0.00d;
		Date startDate = null;
		MboRemote mbo = this.getMboValue().getMbo();

		String assetTypeCode = mbo.getString("udassettypecode");
		if (assetTypeCode != null && "[SQ][SY][SR][SM][SOF][SE][LH][LB][GB][FB]".contains("[" + assetTypeCode + "]")) {
			String type = "";
			MboSetRemote assetTSTransSet = mbo.getMboSet("UDASSETTSTRANS");
			if (!assetTSTransSet.isEmpty() && assetTSTransSet.count() > 0) {
				MboRemote assetTSTrans = assetTSTransSet.getMbo(0);
				workHour += assetTSTrans.getDouble("workhour");
				boxUnit += assetTSTrans.getDouble("boxunit");
				boxTeu += assetTSTrans.getDouble("boxteu");
				electricKwh += assetTSTrans.getDouble("electrickwh");
				oilL += assetTSTrans.getDouble("oill");
				worklock += assetTSTrans.getDouble("worklock");
				control += assetTSTrans.getDouble("control");
				startDate = assetTSTrans.getDate("createdate");
				type = assetTSTrans.getString("type");
			}

			Date endDate = MXServer.getMXServer().getDate();
			if ("UPPER".equalsIgnoreCase(type) && startDate != null && endDate != null) {
				String eqnum = mbo.getString("udeqnum");
				String startDateStr = CommonUtil.getDateFormat(startDate, "yyyy-MM-dd");
				String endDateStr = CommonUtil.getDateFormat(endDate, "yyyy-MM-dd");
				// TOS作业量
				MboSetRemote workSet = mbo.getMboSet("$UDTOSWORKLOAD", "UDTOSWORKLOAD",
						"assetnum ='" + eqnum + "'" + " and to_char(rundate,'yyyy-mm-dd') >= '" + startDateStr + "'"
								+ " and to_char(rundate,'yyyy-mm-dd') <= '" + endDateStr + "'");
				boxUnit += workSet.sum("workload");
				boxTeu += workSet.sum("teuload");
				// TOS运行时长
				MboSetRemote runSet = mbo.getMboSet("$UDTOSRUNTIME", "UDTOSRUNTIME",
						"assetnum ='" + eqnum + "' and to_char(rundate,'yyyy-mm-dd') >= '" + startDateStr
								+ "' and to_char(rundate,'yyyy-mm-dd') <= '" + endDateStr + "'");
				workHour += runSet.sum("runtime");
			}

		} else {
			// 设备再优化
			MboSetRemote eqRunLogSet = mbo.getMboSet("UDEQRUNLOGLINE");
			if (!eqRunLogSet.isEmpty() && eqRunLogSet.count() > 0) {
				workHour += eqRunLogSet.sum("workhour");
				boxUnit += eqRunLogSet.sum("boxunit");
				boxTeu += eqRunLogSet.sum("boxteu");
				electricKwh += eqRunLogSet.sum("electrickwh");
				oilL += eqRunLogSet.sum("oill");
			}
			MboSetRemote tosWorkLoadSet = mbo.getMboSet("UDTOSWORKLOAD");
			if (!tosWorkLoadSet.isEmpty() && tosWorkLoadSet.count() > 0) {
				boxUnit += tosWorkLoadSet.sum("workload");
			}
			MboSetRemote tosRunTimeSet = mbo.getMboSet("UDTOSRUNTIME");
			if (!tosRunTimeSet.isEmpty() && tosRunTimeSet.count() > 0) {
				workHour += tosRunTimeSet.sum("runtime");
			}
			MboSetRemote matUseTrans1005Set = mbo.getMboSet("MATUSETRANS1005");
			if (!matUseTrans1005Set.isEmpty() && matUseTrans1005Set.count() > 0) {
				oilL += (-matUseTrans1005Set.sum("quantity"));
			}
		}

		mbo.setValue("udworkhour", workHour, 11L);
		mbo.setValue("udboxunit", boxUnit, 11L);
		mbo.setValue("udboxteu", boxTeu, 11L);
		mbo.setValue("udelectrickwh", electricKwh, 11L);
		mbo.setValue("udoill", oilL, 11L);
		mbo.setValue("udworklock", worklock, 11L);
		mbo.setValue("udcontrol", control, 11L);
	}

}
