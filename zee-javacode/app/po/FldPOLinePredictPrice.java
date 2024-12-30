package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPOLinePredictPrice extends MboValueAdapter {

	public FldPOLinePredictPrice(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0.0D;
		Mbo mbo = getMboValue().getMbo();
		double udpredictprice = mbo.getDouble("UDPREDICTPRICE");
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		double percentTaxRate = taxrate / 100.0D;
		double predictprice = udpredictprice * (1.0D + percentTaxRate);
		mbo.setValue("udpredicttaxprice", predictprice, 11L);
	}
}
