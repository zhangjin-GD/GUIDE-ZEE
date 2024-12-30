package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldQuotationLineTotalCost extends MboValueAdapter {

	public FldQuotationLineTotalCost(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		double taxrate = 0;
		double totalprice, unitcost;
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		double totalcost = mbo.getDouble("udtotalcost");// 含税总价
		double orderqty = mbo.getDouble("orderqty");// 数量
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
