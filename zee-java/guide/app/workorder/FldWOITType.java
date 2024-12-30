package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWOITType extends MboValueAdapter {

	public FldWOITType(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		String type = mbo.getString("type");
		if (type != null && !type.equalsIgnoreCase("") && (type.equalsIgnoreCase("B") || type.equalsIgnoreCase("E"))) {
			mbo.setFieldFlag("department", 128L, true);
		} else {
			mbo.setFieldFlag("department", 128L, false);
		}
	}
}
