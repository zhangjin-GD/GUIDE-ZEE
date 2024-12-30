package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPOPredictTaxCost extends MboValueAdapter {

	public FldPOPredictTaxCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote polineSet = mbo.getMboSet("POLINE");
		double predicttaxcost = 0;
		if (!polineSet.isEmpty() && polineSet.count() > 0) {
			for (int i = 0; polineSet.getMbo(i) != null; i++) {
				MboRemote poline = polineSet.getMbo(i);
				double orderqty = poline.getDouble("orderqty");
				double udpredicttaxprice = poline.getDouble("udpredicttaxprice");
				predicttaxcost += orderqty * udpredicttaxprice;
			}
		}
		mbo.setValue("udpredicttaxcost", predicttaxcost, 11L);
	}
}
