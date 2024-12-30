package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

/**
 * 
 * @function:INVOICE-此代码未绑定(用于给税、不含税单价、含税单价赋值)
 * @author:zj
 * @date:2023-07-12 16:43:02
 * @modify:
 */
public class UDFldInvoiceLineTotalCost extends MboValueAdapter {

	public UDFldInvoiceLineTotalCost(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		double totalprice, unitcost;
		MboRemote mbo = this.getMboValue().getMbo();
		double totalcost = mbo.getDouble("udtotalcost");// 含税总价
		double invoiceqty = mbo.getDouble("invoiceqty");// 数量
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		taxSet.close();

		double percentTaxRate = taxrate / 100;// 税率

		double linecost = totalcost / (1 + percentTaxRate);// 不含税总价

		if (invoiceqty == 0) {
			totalprice = 0;
			unitcost = 0;
		} else {
			totalprice = totalcost / invoiceqty;// 含税单价
			unitcost = linecost / invoiceqty;// 不含税单价
		}

		double tax1 = totalcost - linecost;// 税额

		mbo.setValue("udtotalprice", totalprice, 11L);
		mbo.setValue("unitcost", unitcost, 2L);
		mbo.setValue("tax1", tax1, 11L);
		
	}
}
