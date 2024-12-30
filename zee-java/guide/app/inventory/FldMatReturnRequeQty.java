package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldMatReturnRequeQty extends MboValueAdapter {

	public FldMatReturnRequeQty(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		Mbo mbo = this.getMboValue().getMbo();
		double returqty = mbo.getDouble("returqty");
		MboSetRemote matDspoLineSet = mbo.getMboSet("UDMATDSPOLINEQTY");
		double orderqty = matDspoLineSet.sum("orderqty");
		double requeqty = returqty - orderqty;
		this.getMboValue().setValue(requeqty, 11L);
	}

	@Override
	public void validate() throws MXException, RemoteException {
		super.validate();
		Mbo mbo = this.getMboValue().getMbo();
		double returqty = mbo.getDouble("returqty");
		double requeqty = mbo.getDouble("requeqty");
		MboSetRemote matDspoLineSet = mbo.getMboSet("UDMATDSPOLINEQTY");
		double orderqty = matDspoLineSet.sum("orderqty");
		if (requeqty <= 0) {
			throw new MXApplicationException("guide", "1097");
		}
		if ((requeqty + orderqty) > returqty) {
			throw new MXApplicationException("guide", "1098");
		}
	}
}
