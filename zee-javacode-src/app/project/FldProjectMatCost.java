package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldProjectMatCost extends MboValueAdapter {

	public FldProjectMatCost(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double linecost = 0.00d;
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote costSet = mbo.getMboSet("matcost");
		if (!costSet.isEmpty()) {
			linecost = costSet.sum("linecost");
		}
		this.getMboValue().setValue(linecost, 11L);
	}
}
