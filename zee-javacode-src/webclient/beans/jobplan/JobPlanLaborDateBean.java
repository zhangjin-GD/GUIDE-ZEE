package guide.webclient.beans.jobplan;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class JobPlanLaborDateBean extends DataBean{

	public void selectlabor() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			Object[] obj = { "主表保存后才能选择员工" };
		    throw new MXApplicationException("udmessage", "error1", obj);
		}
	}
}
