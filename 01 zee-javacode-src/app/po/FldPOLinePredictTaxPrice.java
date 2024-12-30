package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPOLinePredictTaxPrice extends MboValueAdapter {

	public FldPOLinePredictTaxPrice(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		Mbo mbo = this.getMboValue().getMbo();
		double predicttaxprice = mbo.getDouble("udpredicttaxprice");//寄售含税单价
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		double percentTaxRate = taxrate / 100;
		double predictprice = predicttaxprice / (1 + percentTaxRate);
		mbo.setValue("udpredictprice", predictprice, 11L);
	}
}
