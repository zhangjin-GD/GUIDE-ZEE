package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.app.invoice.FldInvoiceLineQty;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 * 
 * @function:INVOICE-此代码已绑定
 * @author:zj
 * @date:2023-07-12 16:43:02
 * @modify:
 */
public class UDFldInvoiceLineQty extends FldInvoiceLineQty {

	public UDFldInvoiceLineQty(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		MboRemote mbo = this.getMboValue().getMbo();
		double unitcost = mbo.getDouble("unitcost");// 不含税单价
		double linecost = mbo.getDouble("linecost");// 不含税总价
		double invoiceqty = mbo.getDouble("invoiceqty");
		if (invoiceqty < 0) {
			Object params[] = { "Quantity cannot be less than 0." };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		}
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (!taxSet.isEmpty() && taxSet.count() > 0) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		taxSet.close();

		double percentTaxRate = taxrate / 100;// 税率

		double totalprice = unitcost * (1 + percentTaxRate);// 含税单价

		double totalcost = totalprice * invoiceqty; // 含税总价

		double tax1 = totalcost - linecost;// 税额

		mbo.setValue("udtotalprice", totalprice, 11L);
		mbo.setValue("udtotalcost", totalcost, 11L);
		mbo.setValue("tax1", tax1, 11L);

	}
}
