package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldInvEntoryVOrderQty extends MboValueAdapter {

	public FldInvEntoryVOrderQty(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		this.getMboValue().setValue(1, 11L);
	}

	@Override
	public void validate() throws MXException, RemoteException {
		super.validate();
		Mbo mbo = this.getMboValue().getMbo();
		double totalqty = mbo.getDouble("totalqty");
		double vorderqty = mbo.getDouble("vorderqty");
		if (vorderqty <= 0) {
			throw new MXApplicationException("guide", "1069");
		}
		if (vorderqty > totalqty) {
			throw new MXApplicationException("guide", "1049");
		}
	}
}
