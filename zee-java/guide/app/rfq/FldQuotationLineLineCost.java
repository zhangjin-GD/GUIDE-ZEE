package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurLineCost;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldQuotationLineLineCost extends FldPurLineCost {

	public FldQuotationLineLineCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		double totalprice2;
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		double udtotalprice1 = mbo.getDouble("udtotalprice");// 含税单价
		double udtotalcost1 = mbo.getDouble("udtotalcost");// 含税总价
		double linecost = mbo.getDouble("linecost");// 不含税总价
		double orderqty = mbo.getDouble("orderqty");// 数量
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}

		double percentTaxRate = taxrate / 100;// 税率

		double totalcost2 = linecost * (1 + percentTaxRate);// 含税总价

		if (orderqty == 0) {
			totalprice2 = 0;
		} else {
			totalprice2 = totalcost2 / orderqty;// 含税单价
		}

		double tax1 = totalcost2 - linecost;// 税额
		
		double priceDiff = udtotalprice1 - totalprice2;

		double priceAbs = Math.abs(priceDiff);
		if (priceAbs > 0.01) {
			mbo.setValue("udtotalprice", totalprice2, 11L);
		}

		double costDiff = udtotalcost1 - totalcost2;
		
		double costAbs = Math.abs(costDiff);
		if (costAbs > 0.01) {
			mbo.setValue("udtotalcost", totalcost2, 11L);
		}

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
