package guide.app.project;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldProconReConnum extends MAXTableDomain {

	public FldProconReConnum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDPROCON", "PROCONNUM=:" + thisAttr);
		String[] FromStr = { "PROCONNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("status in ('APPR')");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		Mbo mbo = this.getMboValue().getMbo();
		String proconrenum = mbo.getString("proconrenum");
		MboSetRemote proConSet = mbo.getMboSet("UDPROCON");
		if (!proConSet.isEmpty()) {
			
			MboRemote proCon = proConSet.getMbo(0);
			String projectnum = proCon.getString("projectnum");
			String budgetnum = proCon.getString("budgetnum");
			String vendor = proCon.getString("vendor");
			String vendorname = proCon.getString("vendorname");
			String currencycode = proCon.getString("currencycode");
			double totalcost = proCon.getDouble("totalcost");
			double tax = proCon.getDouble("tax");
			double pretaxcost = proCon.getDouble("pretaxcost");
			Date deliverydate = proCon.getDate("deliverydate");
			String contactby = proCon.getString("contactby");
			String phone = proCon.getString("phone");
			String bankname = proCon.getString("bankname");
			String bankaccountnum = proCon.getString("bankaccountnum");
			
			mbo.setValue("projectnum", projectnum, 11L);
			mbo.setValue("budgetnum", budgetnum, 11L);
			mbo.setValue("vendor", vendor, 11L);
			mbo.setValue("vendorname", vendorname, 11L);
			mbo.setValue("currencycode", currencycode, 11L);
			mbo.setValue("totalcost", totalcost, 11L);
			mbo.setValue("tax", tax, 11L);
			mbo.setValue("pretaxcost", pretaxcost, 11L);
			mbo.setValue("deliverydate", deliverydate, 11L);
			mbo.setValue("contactby", contactby, 11L);
			mbo.setValue("phone", phone, 11L);
			mbo.setValue("bankname", bankname, 11L);
			mbo.setValue("bankaccountnum", bankaccountnum, 11L);

			MboSetRemote proConLineSet = proCon.getMboSet("UDPROCONLINE");
			if (!proConLineSet.isEmpty()) {

				MboSetRemote mboLineSet = mbo.getMboSet("UDPROCONRELINE");
				if (!mboLineSet.isEmpty()) {
					mboLineSet.deleteAll();
				}

				for (int i = 0; proConLineSet.getMbo(i) != null; i++) {
					MboRemote proConLine = proConLineSet.getMbo(i);

					String proconnum = proConLine.getString("proconnum");
					int proconlinenum = proConLine.getInt("proconlinenum");
					String paytype = proConLine.getString("paytype");
					String payterm = proConLine.getString("payterm");
					Date paiddate = proConLine.getDate("paiddate");
					double paycost = proConLine.getDouble("paycost");
					String paystatus = proConLine.getString("paystatus");

					MboRemote mboLine = mboLineSet.add();
					mboLine.setValue("proconnum", proconnum, 11L);
					mboLine.setValue("proconlinenum", proconlinenum, 11L);
					mboLine.setValue("paytype", paytype, 11L);
					mboLine.setValue("payterm", payterm, 11L);
					mboLine.setValue("paiddate", paiddate, 11L);
					mboLine.setValue("paycost", paycost, 2L);
					mboLine.setValue("paystatus", paystatus, 11L);
					mboLine.setValue("proconrenum", proconrenum, 11L);
				}
			}
		}

		if (this.getMboValue().isNull()) {
			mbo.setValueNull("vendor", 11L);
			mbo.setValueNull("vendorname", 11L);
			mbo.setValueNull("currencycode", 11L);
			mbo.setValueNull("deliverydate", 11L);
			mbo.setValueNull("contactby", 11L);
			mbo.setValueNull("phone", 11L);
			mbo.setValueNull("bankname", 11L);
			mbo.setValueNull("bankaccountnum", 11L);
			mbo.setValue("totalcost", 0, 11L);
			mbo.setValue("tax", 0, 11L);
			mbo.setValue("pretaxcost", 0, 11L);

			MboSetRemote mboLineSet = mbo.getMboSet("UDPROCONRELINE");
			if (!mboLineSet.isEmpty()) {
				mboLineSet.deleteAll();
			}
		}
	}
}
