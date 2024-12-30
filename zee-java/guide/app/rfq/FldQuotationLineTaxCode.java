package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.common.FldCommonTaxCode;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldQuotationLineTaxCode extends FldCommonTaxCode {

	public FldQuotationLineTaxCode(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		Mbo mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			MboRemote parent = owner.getOwner();
			String vendor = owner.getString("vendor");
			String appname = parent.getThisMboSet().getApp();
			if (parent != null) {
				String company = parent.getString("udcompany");
				if ("AE03ADT".equalsIgnoreCase(company)) {
					listSet.setWhere("taxcode like 'J%'");
				} else if ("PE03CP".equalsIgnoreCase(company)) {
					listSet.setWhere("taxcode like '%R'");
				} else if ("GR02PCT".equalsIgnoreCase(company)) {
					MboSetRemote companiesSet = MXServer.getMXServer().getMboSet("COMPANIES",
							MXServer.getMXServer().getSystemUserInfo());
					companiesSet.setWhere("COMPANY='" + vendor + "'");
					companiesSet.reset();
					if (appname.equalsIgnoreCase("UDRFQMAT")) {
						if (!vendor.equalsIgnoreCase("") && vendor != null) {
							if (!companiesSet.isEmpty() && companiesSet.count() > 0) {
								String address2 = companiesSet.getMbo(0).getString("address2");
								if (!address2.equalsIgnoreCase("") && address2 != null) {
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
					} else if (appname.equalsIgnoreCase("UDRFQSER")) {
						if (!vendor.equalsIgnoreCase("") && vendor != null) {
							if (!companiesSet.isEmpty() && companiesSet.count() > 0) {
								String address2 = companiesSet.getMbo(0).getString("address2");
								if (!address2.equalsIgnoreCase("") && address2 != null) {
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
					} else if (appname.equalsIgnoreCase("UDRFQFIX")) {
						if (!vendor.equalsIgnoreCase("") && vendor != null) {
							if (!companiesSet.isEmpty() && companiesSet.count() > 0) {
								String address2 = companiesSet.getMbo(0).getString("address2");
								if (!address2.equalsIgnoreCase("") && address2 != null) {
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
				}

				else {
					listSet.setWhere(
							"taxcode not like 'J%' and taxcode not like '%R' and taxcode not in ('1A','1B','1C','1D','1E','1G','2A','2B','2C','2D','2E','2F','2G','2H','4A','4C','4D','4E','4F')");
				}
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
		MboRemote parent = mbo.getOwner();
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

		if (parent != null && parent instanceof UDRFQVendor) {
			parent.getMboSet("QUOTATIONLINEVENDOR").resetQbe();
			double totalcostSum = mbo.getThisMboSet().sum("udtotalcost");
			double tax1Sum = mbo.getThisMboSet().sum("tax1");
			double linecostSum = mbo.getThisMboSet().sum("linecost");

			parent.setValue("udtotalcost", totalcostSum, 11L);
			parent.setValue("udlinecost", linecostSum, 11L);
			parent.setValue("udtax1", tax1Sum, 11L);
		}
	}
}
