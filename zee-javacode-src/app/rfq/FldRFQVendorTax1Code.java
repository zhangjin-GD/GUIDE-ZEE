package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldRFQVendorTax1Code extends MAXTableDomain {

	public FldRFQVendorTax1Code(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("TAX", "taxcode=:" + thisAttr);
		String[] FromStr = { "taxcode" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		Mbo mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String vendor = mbo.getString("vendor");
		if (owner != null) {
			String appname = owner.getThisMboSet().getApp();
			String company = owner.getString("udcompany");
			if ("AE03ADT".equalsIgnoreCase(company)) {
				listSet.setWhere("typecode = 1 and taxcode like 'J%'");
			} else if ("PE03CP".equalsIgnoreCase(company)) {
				listSet.setWhere("typecode = 1 and taxcode like '%R'");
			} else if ("GR02PCT".equalsIgnoreCase(company)) {// GR02PCT
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
			} else {
				listSet.setWhere(
						"taxcode not like 'J%' and taxcode not like '%R' and taxcode not in ('1A','1B','1C','1D','1E','1G','2A','2B','2C','2D','2E','2F','2G','2H','4A','4C','4D','4E','4F')");
			}
		}
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		String tax1code = this.getMboValue().getString();
		MboSetRemote quotaTionLineSet = mbo.getMboSet("QUOTATIONLINEVENDOR");
		if (!quotaTionLineSet.isEmpty() && quotaTionLineSet.count() > 0) {
			for (int i = 0; quotaTionLineSet.getMbo(i) != null; i++) {
				MboRemote quotaTionLine = quotaTionLineSet.getMbo(i);
				quotaTionLine.setValue("tax1code", tax1code, 2L);
			}
		}
	}
}
