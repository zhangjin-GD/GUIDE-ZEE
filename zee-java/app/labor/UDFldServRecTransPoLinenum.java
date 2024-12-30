package guide.app.labor;

import java.rmi.RemoteException;

import psdi.app.labor.FldServRecTransPoLinenum;
import psdi.app.labor.ServRecTrans;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldServRecTransPoLinenum extends FldServRecTransPoLinenum {

	public UDFldServRecTransPoLinenum(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		if (!this.getMboValue().isNull()) {
			ServRecTrans servMbo = (ServRecTrans) this.getMboValue().getMbo();
			
			MboRemote poline = servMbo.getPOLine();
			String udprojectnum = poline.getString("udprojectnum");
			String udbudgetnum = poline.getString("udbudgetnum");

			servMbo.setValue("udprojectnum", udprojectnum, 11L);
			servMbo.setValue("udbudgetnum", udbudgetnum, 11L);
		}
	}
}
