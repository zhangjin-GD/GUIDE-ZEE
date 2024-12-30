package guide.app.woremain;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWoreTaskVWoDesc extends MboValueAdapter {

	public FldWoreTaskVWoDesc(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote mbo = this.getMboValue().getMbo();
		String wodesc = mbo.getString("wodesc");
		this.getMboValue().setValue(wodesc, 11L);
	}
}
