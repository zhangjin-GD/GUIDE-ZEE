package guide.app.contract;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldConLineDiscountPrice extends MboValueAdapter {
	public UDFldConLineDiscountPrice(MboValue mbv) throws MXException,
			RemoteException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0.0D;
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		MboSetRemote taxSet = mbo.getMboSet("TAX");

		if (!taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		double percentTaxRate = taxrate / 100;// 不含税价=含税价÷（1+税率）
		double orderqty = mbo.getDouble("orderqty");// 数量
		double unitcost = mbo.getDouble("unitcost");// 不含税单价
		double totalunitcost = unitcost * (1 + percentTaxRate);// 含税单价
		double totallinecost = orderqty * totalunitcost;// 含税总价
		double linecost = orderqty * unitcost;// 不含税总价
		double tax1 = totallinecost - linecost;
		double uddiscountprice = mbo.getDouble("uddiscountprice");
		if (unitcost == 0) {
		      Object[] params = { "Unitcost(Tax Excl.) cannot be 0" };
		      throw new MXApplicationException("instantmessaging", "tsdimexception",params);
		}
		double uddiscount = uddiscountprice / unitcost;// 填写折后单价，计算折扣

		mbo.setValue("totallinecost", totallinecost, 11L);
		mbo.setValue("totalunitcost", totalunitcost, 11L);
		mbo.setValue("unitcost", unitcost, 11L);
		mbo.setValue("linecost", linecost, 11L);
		mbo.setValue("tax1", tax1, 11L);
		mbo.setValue("uddiscount", uddiscount, 11L);
		mbo.setValue("uddiscountprice", uddiscountprice, 11L);

		if (parent != null && parent instanceof Contract) {
			parent.getMboSet("UDCONTRACTLINE").resetQbe();
			double totallinecostsum = mbo.getThisMboSet().sum("totallinecost");
			double linecostsum = mbo.getThisMboSet().sum("linecost");
			parent.setValue("totalcost", totallinecostsum, 11L);
			parent.setValue("pretaxtotal", linecostsum, 11L);
		}
	}
}