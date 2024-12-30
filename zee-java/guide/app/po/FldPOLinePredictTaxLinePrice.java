package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPOLinePredictTaxLinePrice extends MboValueAdapter {

	public FldPOLinePredictTaxLinePrice(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		Mbo mbo = this.getMboValue().getMbo();
		double orderqty = mbo.getDouble("orderqty");
		double udpredicttaxprice = mbo.getDouble("udpredicttaxprice");
		double predicttaxlineprice = orderqty * udpredicttaxprice;
		mbo.setValue("udpredicttaxlineprice", predicttaxlineprice, 11L);
	}
}
