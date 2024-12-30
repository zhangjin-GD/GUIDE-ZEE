package guide.app.asset;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.common.CommonUtil;
import guide.app.project.ProCon;
import psdi.app.asset.Asset;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldVAssetTstransCreateDate extends MboValueAdapter {

	public FldVAssetTstransCreateDate(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double workHour = 0.00d;
		double boxUnit = 0.00d;
		double boxTeu = 0.00d;
		double electricKwh = 0.00d;
		double oilL = 0.00d;
		double worklock = 0.00d;
		double control = 0.00d;
		Date startDate = null;
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof Asset) {
			String assetTypeCode = owner.getString("udassettypecode");
			if (assetTypeCode != null
					&& "[SQ][SY][SR][SM][SOF][SE][LH][LB][GB][FB]".contains("[" + assetTypeCode + "]")) {
				String type = "";
				MboSetRemote assetTSTransSet = owner.getMboSet("UDASSETTSTRANS");
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
				Date endDate = mbo.getDate("createdate");
				if ("UPPER".equalsIgnoreCase(type) && startDate != null && endDate != null) {
					String eqnum = owner.getString("udeqnum");
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
				mbo.setValue("workhour", workHour, 11L);
				mbo.setValue("boxunit", boxUnit, 11L);
				mbo.setValue("boxteu", boxTeu, 11L);
				mbo.setValue("electrickwh", electricKwh, 11L);
				mbo.setValue("oill", oilL, 11L);
				mbo.setValue("worklock", worklock, 11L);
				mbo.setValue("control", control, 11L);
			}
		}
	}
}
