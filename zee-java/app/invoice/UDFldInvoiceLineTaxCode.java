package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.app.common.FldCommonTaxCode;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 * 
 * @function:INVOICE-此代码已绑定
 * @author:zj
 * @date:2023-07-12 16:43:02
 * @modify:
 */
public class UDFldInvoiceLineTaxCode extends FldCommonTaxCode {

	public UDFldInvoiceLineTaxCode(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		listSet.setWhere(" udcompany='ZEE' ");
		return listSet;
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		double udtotalprice, udtotalcost;
		MboRemote mbo = this.getMboValue().getMbo();
		double linecost = mbo.getDouble("linecost");// 不含税总价
		double invoiceqty = mbo.getDouble("invoiceqty");
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		taxSet.close();
		double percentTaxRate = taxrate / 100;// 税率
		
		udtotalcost = linecost * (1 + percentTaxRate);// 含税总价

		if (invoiceqty == 0) {
			udtotalprice = 0;
			udtotalcost = 0;
		} else {
			udtotalprice = udtotalcost / invoiceqty;// 含税单价
		}

		double tax1 = udtotalcost - linecost;// 税额

		mbo.setValue("udtotalprice", udtotalprice, 11L);
		mbo.setValue("udtotalcost", udtotalcost, 11L);
		mbo.setValue("tax1", tax1, 11L);
		
//		double linecost = totalcost / (1 + percentTaxRate);// 不含税总价
//
//		if (invoiceqty == 0) {
//			totalprice = 0;
//			unitcost = 0;
//		} else {
//			totalprice = totalcost / invoiceqty;// 含税单价
//			unitcost = linecost / invoiceqty;// 不含税单价
//		}
//
//		double tax1 = totalcost - linecost;// 税额
//
//		mbo.setValue("udtotalprice", totalprice, 11L);
//		mbo.setValue("udtotalcost", totalprice * invoiceqty, 11L);
//		mbo.setValue("unitcost", unitcost, 2L);
//		mbo.setValue("tax1", tax1, 11L);

	}
}
