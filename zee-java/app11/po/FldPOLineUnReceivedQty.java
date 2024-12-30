package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPOLineUnReceivedQty extends MboValueAdapter {

	public FldPOLineUnReceivedQty(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		Mbo mbo = this.getMboValue().getMbo();
		double orderqty = mbo.getDouble("orderqty");
		double receivedqty = mbo.getDouble("receivedqty");
		double unreceivedqty = orderqty - receivedqty;
		mbo.setValue("udunreceivedqty", unreceivedqty, 11L);
	}
}
