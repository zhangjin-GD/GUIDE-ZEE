package guide.app.item;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldItemCpVTotalPrice extends MboValueAdapter {

	public FldItemCpVTotalPrice(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		this.getMboValue().setValue(0, 11L);
	}

	@Override
	public void validate() throws MXException, RemoteException {
		super.validate();
		Mbo mbo = this.getMboValue().getMbo();
		double totalprice = mbo.getDouble("vtotalprice");
		if (totalprice < 0) {
			throw new MXApplicationException("guide", "1035");
		}
	}
}
