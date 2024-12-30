package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.app.invoice.FldInvoiceLineLineCost;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 * 
 * @function:INVOICE-此代码未绑定(用于给税、含税单价、含税总价赋值)
 * @author:zj
 * @date:2023-07-12 16:43:02
 * @modify:
 */
public class UDFldInvoiceLineLineCost extends FldInvoiceLineLineCost {

	public UDFldInvoiceLineLineCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		double totalprice2;
		MboRemote mbo = this.getMboValue().getMbo();
		double udtotalprice1 = mbo.getDouble("udtotalprice");// 含税单价
		double udtotalcost1 = mbo.getDouble("udtotalcost");// 含税总价
		double linecost = mbo.getDouble("linecost");// 不含税总价
		double invoiceqty = mbo.getDouble("invoiceqty");// 数量
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		taxSet.close();

		double percentTaxRate = taxrate / 100;// 税率

		double totalcost2 = linecost * (1 + percentTaxRate);// 含税总价

		if (invoiceqty == 0) {
			totalprice2 = 0;
		} else {
			totalprice2 = totalcost2 / invoiceqty;// 含税单价
		}

		double tax1 = totalcost2 - linecost;

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

	}
}
