package guide.app.pr;

import java.rmi.RemoteException;

import psdi.app.common.FldCommonTaxCode;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldPrLineTaxCode extends FldCommonTaxCode {

	public FldPrLineTaxCode(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		Mbo mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String company = owner.getString("udcompany");
			String vendor = owner.getString("vendor");
			String appname = owner.getThisMboSet().getApp();
			if ("AE03ADT".equalsIgnoreCase(company)) {
				listSet.setWhere("taxcode like 'J%'");
			} else if ("PE03CP".equalsIgnoreCase(company)) {
				listSet.setWhere("taxcode like '%R'");
			} else if ("GR02PCT".equalsIgnoreCase(company)) {
				MboSetRemote companiesSet = MXServer.getMXServer().getMboSet("COMPANIES",
						MXServer.getMXServer().getSystemUserInfo());
				companiesSet.setWhere("COMPANY='" + vendor + "'");
				companiesSet.reset();
				if (appname.equalsIgnoreCase("UDPRMAT")) {
					if (!vendor.equalsIgnoreCase("") && vendor != null) {
						if (!companiesSet.isEmpty() && companiesSet.count() > 0) {
							String address2 = companiesSet.getMbo(0).getString("address2");
							if (!address2.equalsIgnoreCase("") || address2 != null) {
								if (address2.equalsIgnoreCase("GR")) {
									listSet.setWhere("taxcode in ('1A','1G','2A','2B','2C','2D')");
								} else if (address2.equalsIgnoreCase("E.U.")) {
									listSet.setWhere("taxcode in ('1B','1C','2E','2F')");
								} else if (address2.equalsIgnoreCase("Non E.U")) {
									listSet.setWhere("taxcode in ('1D','1E','2G','2H')");
								} else {
									listSet.setWhere(
											"taxcode in ('1A','1B','1C','1D','1E','1G','2A','2B','2C','2D','2E','2F','2G','2H')");
								}
							} else {
								listSet.setWhere(
										"taxcode in ('1A','1B','1C','1D','1E','1G','2A','2B','2C','2D','2E','2F','2G','2H')");
							}
						}
					} else {
						listSet.setWhere(
								"taxcode in ('1A','1B','1C','1D','1E','1G','2A','2B','2C','2D','2E','2F','2G','2H')");
					}
				} else if (appname.equalsIgnoreCase("UDPRSER")) {
					if (!vendor.equalsIgnoreCase("") && vendor != null) {
						if (!companiesSet.isEmpty() && companiesSet.count() > 0) {
							String address2 = companiesSet.getMbo(0).getString("address2");
							if (!address2.equalsIgnoreCase("") || address2 != null) {
								if (address2.equalsIgnoreCase("GR")) {
									listSet.setWhere("taxcode in ('2A','2B','2C','2D')");
								} else if (address2.equalsIgnoreCase("E.U.")) {
									listSet.setWhere("taxcode in ('2E','2F')");
								} else if (address2.equalsIgnoreCase("Non E.U")) {
									listSet.setWhere("taxcode in ('2G','2H')");
								} else {
									listSet.setWhere("taxcode in ('2A','2B','2C','2D','2E','2F','2G','2H')");
								}
							} else {
								listSet.setWhere("taxcode in ('2A','2B','2C','2D','2E','2F','2G','2H')");
							}
						}
					} else {
						listSet.setWhere("taxcode in ('2A','2B','2C','2D','2E','2F','2G','2H')");
					}
				} else if (appname.equalsIgnoreCase("UDPRFIX")) {
					if (!vendor.equalsIgnoreCase("") && vendor != null) {
						if (!companiesSet.isEmpty() && companiesSet.count() > 0) {
							String address2 = companiesSet.getMbo(0).getString("address2");
							if (!address2.equalsIgnoreCase("") || address2 != null) {
								if (address2.equalsIgnoreCase("GR")) {
									listSet.setWhere("taxcode in ('4A')");
								} else if (address2.equalsIgnoreCase("E.U.")) {
									listSet.setWhere("taxcode in ('4C','4D')");
								} else if (address2.equalsIgnoreCase("Non E.U")) {
									listSet.setWhere("taxcode in ('4E','4F')");
								} else {
									listSet.setWhere("taxcode in ('4A','4C','4D','4E','4F')");
								}
							} else {
								listSet.setWhere("taxcode in ('4A','4C','4D','4E','4F')");
							}
						}
					} else {
						listSet.setWhere("taxcode in ('4A','4C','4D','4E','4F')");
					}
				}
			} else if ("ZEE".equalsIgnoreCase(company)) { //ZEE看到自己的税代码 2023-10-09 14:44:16
				listSet.setWhere(" udcompany='ZEE' ");
			} else {
				listSet.setWhere("taxcode not like 'J%' and taxcode not like '%R'");
			}
		}
		return listSet;
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		double totalprice, unitcost;
		Mbo mbo = this.getMboValue().getMbo();
		double totalcost = mbo.getDouble("udtotalcost");// 含税总价
		double orderqty = mbo.getDouble("orderqty");
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		double percentTaxRate = taxrate / 100;// 税率

		double linecost = totalcost / (1 + percentTaxRate);// 不含税总价

		if (orderqty == 0) {
			totalprice = 0;
			unitcost = 0;
		} else {
			totalprice = totalcost / orderqty;// 含税单价
			unitcost = linecost / orderqty;// 不含税单价
		}

		double tax1 = totalcost - linecost;// 税额

		mbo.setValue("udtotalprice", totalprice, 11L);
		mbo.setValue("unitcost", unitcost, 2L);
		mbo.setValue("tax1", tax1, 11L);
	}
}
