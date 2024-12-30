package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldFailClassType extends MboValueAdapter {

	public FldFailClassType(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String type = this.getMboValue().getString();
		MboSetRemote typeSet = mbo.getMboSet("type");
		if (!typeSet.isEmpty() && typeSet.count() > 0) {
			String desc = typeSet.getMbo(0).getString("description");
			if (mbo.isNull("failclassnum")) {
				mbo.setValue("failclassnum", type, 11L);
				mbo.setValue("description", desc, 11L);
			}
		}
	}
}
