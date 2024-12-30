package guide.app.po;

import java.rmi.RemoteException;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPOLineTotalPrice extends MboValueAdapter {

	public FldPOLineTotalPrice(MboValue mbv) {
		super(mbv);
	}

	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		double totalcost, unitcost;
		Mbo mbo = getMboValue().getMbo();
		double totalprice = mbo.getDouble("udtotalprice");// 含税单价
		double orderqty = mbo.getDouble("orderqty");// 数量
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}

		if (orderqty == 0) {
			totalcost = 0;
		} else {
			totalcost = totalprice * orderqty;// 含税总价
		}

		double percentTaxRate = taxrate / 100;// 税率

		double linecost = totalcost / (1 + percentTaxRate);// 不含税总价

		if (orderqty == 0) {
			unitcost = 0;
		} else {
			unitcost = linecost / orderqty;// 不含税单价
		}

		double tax1 = totalcost - linecost;// 税额

		mbo.setValue("udtotalcost", totalcost, 11L);
		mbo.setValue("unitcost", unitcost, 2L);
		mbo.setValue("tax1", tax1, 11L);

		if (totalprice > 0) {
			mbo.setValue("udpredicttaxprice", totalprice, 2L);
		}
	}
}
