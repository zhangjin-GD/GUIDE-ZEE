package guide.webclient.beans.inventory;

import guide.app.fixed.FixEd;
import guide.app.fixed.FixEdSet;
import guide.app.inventory.UDMatRecTrans;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

/**
 * ZEE - 接收时，创建固定资产台账
 * 2025-1-14-14:00
 */
public class UDCreateFixAssetZEEDataBean extends DataBean{
	
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote mboApp = this.app.getAppBean().getMbo();
		if (mboApp.toBeSaved()) {
			Object params[] = { "Notice, please save first!" };
			throw new MXApplicationException("instantmessaging", "tsdimexception",params);
		}
		MboRemote mbo = getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDMatRecTrans && owner.getString("tostoreloc").startsWith("ZEE")) {
			int matrectransid = owner.getInt("matrectransid");
			MboSetRemote fixEdSet = mboApp.getMboSet("$UDFIXED", "UDFIXED", "matrectransid='" + matrectransid + "'");
			if (!fixEdSet.isEmpty() && fixEdSet.count() > 0) {
				List<String> list = new ArrayList<String>();
				for (int i = 0; fixEdSet.getMbo(i) != null; i++) {
					MboRemote fixEd = fixEdSet.getMbo(i);
					String fixassetnum = fixEd.getString("fixassetnum");
					list.add(fixassetnum);
				}
				if (list.size() > 0) {
					Object str[] = { "Notice, existing fix asset: "+ list+"! "};
					throw new MXApplicationException("instantmessaging", "tsdimexception",str);
				}
			}
			String issuetype = owner.getString("issuetype");
			if ("RECEIPT".equalsIgnoreCase(issuetype)) {
				double qtyReceipt = owner.getDouble("quantity");
				MboSetRemote returnSet = owner.getMboSet("RETURNRECEIPTS");
				double qtyReturn = returnSet.sum("quantity");// 退回数量
				double qty = qtyReceipt + qtyReturn;
				if (qty <= 0) {
					Object params[] = { "Notice, the received quantity of this material is abnormal! Receipt quantity is minus or zero! " };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params);
				}
			} else {				
					Object params[] = { "Notice, This material is in return and cannot be created as an fix asset!" };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params);
			}
		}
	}
	
	public synchronized int execute()  throws MXException, RemoteException {
		MboRemote mboApp = this.app.getAppBean().getMbo();
		MboRemote mbo = getMbo();
		MboRemote owner = mbo.getOwner();
		List<String> list = new ArrayList<String>();
		if (owner != null && owner instanceof UDMatRecTrans && owner.getString("tostoreloc").startsWith("ZEE")) {
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
					list.add(fixassetnum);
				}
				owner.setValue("udfixasset", "1",11L);
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Result: Create fix asset: " + list + "!", 1);
			} else {
				if (list.size() > 0) {
					Object str[] = { "Notice, Existing fix asset: "+ list+"! "};
					throw new MXApplicationException("instantmessaging", "tsdimexception",str);
				}
			}
		}	
		return super.execute();
	}
	
}
