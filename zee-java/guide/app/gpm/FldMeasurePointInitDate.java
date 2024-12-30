package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMeasurePointInitDate extends MboValueAdapter {

	public FldMeasurePointInitDate(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String readingtype = mbo.getString("readingtype");
		String pmtype = mbo.getString("pmtype");
		if (owner != null && owner instanceof UDGpm) {
			if ("DELTA".equalsIgnoreCase(readingtype)) {
				if ("OIL".equalsIgnoreCase(pmtype)) {
					MboSetRemote oilmatuseSet = owner.getMboSet("OILMATUSE");
					double quantity = oilmatuseSet.sum("quantity");
					quantity = Math.abs(quantity);
					mbo.setValue("value", quantity, 2L);
				} else if ("HOUR".equalsIgnoreCase(pmtype)) {
					String sql = "assetnum=:assetnum";
					if (!mbo.isNull("initdate")) {
						sql += " and rundate>:initdate";
					}
					MboSetRemote hourtosrunSet = mbo.getMboSet("$UDTOSRUNTIME", "UDTOSRUNTIME", sql);
					double quantity = hourtosrunSet.sum("runtime");
					quantity = Math.abs(quantity);
					mbo.setValue("value", quantity, 2L);
				} else {
					MboSetRemote measureMentSet = mbo.getMboSet("UDMEASUREMENT");
					double value = measureMentSet.sum("value");
					value = Math.abs(value);
					mbo.setValue("value", value, 2L);
				}
			} else {
				if ("OIL".equalsIgnoreCase(pmtype)) {
					MboSetRemote oilmatuseSet = owner.getMboSet("OILMATUSE");
					oilmatuseSet.setOrderBy("transdate desc");
					oilmatuseSet.reset();
					double quantity = 0;
					if (!oilmatuseSet.isEmpty() && oilmatuseSet.count() > 0) {
						MboRemote oilmatuse = oilmatuseSet.getMbo(0);
						quantity = oilmatuse.getDouble("quantity");
						quantity = Math.abs(quantity);
					}
					mbo.setValue("value", quantity, 2L);
				} else if ("HOUR".equalsIgnoreCase(pmtype)) {
					String sql = "assetnum=:assetnum";
					if (!mbo.isNull("initdate")) {
						sql += " and rundate>:initdate";
					}
					MboSetRemote hourtosrunSet = mbo.getMboSet("$UDTOSRUNTIME", "UDTOSRUNTIME", sql);
					hourtosrunSet.setOrderBy("rundate desc");
					hourtosrunSet.reset();
					double value = 0;
					if (!hourtosrunSet.isEmpty() && hourtosrunSet.count() > 0) {
						MboRemote measureMent = hourtosrunSet.getMbo(0);
						value = measureMent.getDouble("runtime");
						value = Math.abs(value);
					}
					mbo.setValue("value", value, 2L);
				} else {
					MboSetRemote measureMentSet = mbo.getMboSet("UDMEASUREMENT");
					measureMentSet.setOrderBy("measuredate desc");
					measureMentSet.reset();
					double value = 0;
					if (!measureMentSet.isEmpty() && measureMentSet.count() > 0) {
						MboRemote measureMent = measureMentSet.getMbo(0);
						value = measureMent.getDouble("value");
						value = Math.abs(value);
					}
					mbo.setValue("value", value, 2L);
				}
			}
		}
	}
}
