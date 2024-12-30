package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.fixed.FixEd;
import guide.app.fixed.FixEdSet;
import guide.app.inventory.UDMatRecTrans;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelMatRecToFixEdDateBean extends DataBean {

	@Override
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote mboApp = this.app.getAppBean().getMbo();
		if (mboApp.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		MboRemote mbo = getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDMatRecTrans) {
			int matrectransid = owner.getInt("matrectransid");
			MboSetRemote fixEdSet = mboApp.getMboSet("$UDFIXED", "UDFIXED", "matrectransid='" + matrectransid + "'");
			if (!fixEdSet.isEmpty() && fixEdSet.count() > 0) {
				StringBuffer buf = new StringBuffer();
				for (int i = 0; fixEdSet.getMbo(i) != null; i++) {
					MboRemote fixEd = fixEdSet.getMbo(i);
					String fixassetnum = fixEd.getString("fixassetnum");
					buf.append(fixassetnum).append(",");
				}
				if (buf.length() > 0) {
					String params = buf.substring(0, buf.length() - 1);
					Object[] obj = { params };
					throw new MXApplicationException("guide", "1131", obj);
				}
			}
			String issuetype = owner.getString("issuetype");
			if ("RECEIPT".equalsIgnoreCase(issuetype)) {
				double qtyReceipt = owner.getDouble("quantity");
				MboSetRemote returnSet = owner.getMboSet("RETURNRECEIPTS");
				double qtyReturn = returnSet.sum("quantity");// 退回数量
				double qty = qtyReceipt + qtyReturn;
				if (qty <= 0) {
					throw new MXApplicationException("guide", "1133");
				}
			} else {
				throw new MXApplicationException("guide", "1132");
			}
		}
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mboApp = this.app.getAppBean().getMbo();
		MboRemote mbo = getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDMatRecTrans) {
			String deptmg = mbo.getString("deptmg");
			String administrator = mbo.getString("administrator");
			int matrectransid = owner.getInt("matrectransid");
			String vendor = owner.getString("porev.vendor");
			String currency = owner.getString("porev.udcurrency");
			String itemnum = owner.getString("itemnum");
			String description = owner.getString("description");
			String modelnum = owner.getString("item.udmodelnum");
			String specs = owner.getString("item.udspecs");
			double unitcost = owner.getDouble("unitcost");
			String tostoreloc = owner.getString("tostoreloc");
			String tobin = owner.getString("tobin");
			String useby = owner.getString("enterby");
			Date actualdate = owner.getDate("actualdate");
			String remark = owner.getString("remark");

			double qtyReceipt = owner.getDouble("quantity");// 接收数量
			MboSetRemote returnSet = owner.getMboSet("RETURNRECEIPTS");
			double qtyReturn = returnSet.sum("quantity");// 退回数量
			double qty = qtyReceipt + qtyReturn;
			FixEdSet fixedSet = (FixEdSet) mboApp.getMboSet("$UDFIXED", "UDFIXED",
					"matrectransid ='" + matrectransid + "'");
			if (fixedSet.isEmpty()) {
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < qty; i++) {
					FixEd fixed = (FixEd) fixedSet.add(2L);
					fixed.setValue("matrectransid", matrectransid, 11L);
					fixed.setValue("useby", useby, 2L);
					fixed.setValue("deptmg", deptmg, 11L);
					fixed.setValue("administrator", administrator, 11L);
					fixed.setValue("itemnum", itemnum, 11L);
					fixed.setValue("description", description, 11L);
					fixed.setValue("modelnum", modelnum, 11L);
					fixed.setValue("specs", specs, 11L);
					fixed.setValue("vendor", vendor, 11L);
					fixed.setValue("currencycode", currency, 11L);
					fixed.setValue("purchasedate", actualdate, 11L);
					fixed.setValue("quantity", 1, 11L);
					fixed.setValue("originalvalue", unitcost, 11L);
					fixed.setValue("remark", remark, 11L);
					fixed.setValue("location", tostoreloc + " / " + tobin, 11L);
					fixed.setValue("status", "5", 11L);
					String fixassetnum = fixed.getString("fixassetnum");
					buf.append(fixassetnum).append(",");
				}
				if (buf.length() > 0) {
					String params = buf.substring(0, buf.length() - 1);
					Object[] obj = { params };
					owner.getThisMboSet().addWarning(new MXApplicationException("guide", "1131", obj));
				}
			} else {
				throw new MXApplicationException("guide", "1118");
			}
		}
		return super.execute();
	}
}
