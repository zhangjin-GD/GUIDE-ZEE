package guide.app.project;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class ProBidVendor extends Mbo implements MboRemote {

	public ProBidVendor(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof ProBid) {
			String probidnum = parent.getString("probidnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("probidnum", probidnum, 11L);
			this.setValue("linenum", linenum, 11L);
			this.setValue("totalcost", 0, 11L);
			this.setValue("tax", 0, 11L);
			this.setValue("pretaxcost", 0, 11L);
		}
	}

	public void createProCon() throws RemoteException, MXException {
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof ProBid) {
			MboSetRemote probidVendorSet = this.getMboSet("PROBIDISAWARDED");
			if (probidVendorSet != null && probidVendorSet.count() == 1) {
				MboRemote probidVendor = probidVendorSet.getMbo(0);
				String contactby = "", phone = "", bankname = "", bankaccountnum = "";
				String probidnum = probidVendor.getString("probidnum");
				String projectnum = probidVendor.getString("udprobid.projectnum");
				String vendor = probidVendor.getString("vendor");
				String vendorname = probidVendor.getString("vendorname");
				String currencycode = probidVendor.getString("currencycode");
				double totalcost = probidVendor.getDouble("totalcost");
				double tax = probidVendor.getDouble("tax");
				double pretaxcost = probidVendor.getDouble("pretaxcost");
				Date deliverydate = probidVendor.getDate("deliverydate");

				MboSetRemote vendorSet = probidVendor.getMboSet("VENDOR");
				if (!vendorSet.isEmpty()) {
					MboRemote vendorMbo = vendorSet.getMbo(0);
					contactby = vendorMbo.getString("contact");// 联系人
					phone = vendorMbo.getString("phone");// 联系人手机号

					MboSetRemote bankinfoSet = vendorMbo.getMboSet("UDBANKINFOISDEFAULT");// 银行信息
					if (!bankinfoSet.isEmpty()) {
						MboRemote bankinfo = bankinfoSet.getMbo(0);
						bankname = bankinfo.getString("bankname");
						bankaccountnum = bankinfo.getString("bankaccountnum");
					}
				}

				ProConSet proconSet = (ProConSet) probidVendor.getMboSet("UDPROCON");
				if (proconSet.isEmpty() && proconSet.count() == 0) {
					ProCon procon = (ProCon) proconSet.add();
					procon.setValue("probidnum", probidnum, 11L);
					procon.setValue("projectnum", projectnum, 11L);
					procon.setValue("vendor", vendor, 11L);
					procon.setValue("currencycode", currencycode, 11L);
					procon.setValue("totalcost", totalcost, 11L);
					procon.setValue("tax", tax, 11L);
					procon.setValue("pretaxcost", pretaxcost, 11L);
					procon.setValue("deliverydate", deliverydate, 11L);
					procon.setValue("vendorname", vendorname, 11L);
					procon.setValue("contactby", contactby, 11L);
					procon.setValue("phone", phone, 11L);
					procon.setValue("bankname", bankname, 11L);
					procon.setValue("bankaccountnum", bankaccountnum, 11L);
					proconSet.save();
				} else {
					Object[] obj = { "温馨提示：已创建项目合同！" };
					throw new MXApplicationException("udmessage", "error1", obj);
				}

			} else {
				Object[] obj = { "温馨提示：未授予或已授予多个供应商！" };
				throw new MXApplicationException("udmessage", "error1", obj);
			}
		}
	}
}