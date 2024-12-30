package guide.webclient.beans.contract;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;
import psdi.webclient.system.controller.WebClientEvent;

public class ContractAppBean extends AppBean {

	public int setValueStatus() throws RemoteException, MXException {
		MboRemote mbo = app.getAppBean().getMbo();
		WebClientEvent event = clientSession.getCurrentEvent();
		String eventValue = event.getValue().toString();
		if (eventValue == null || eventValue.equalsIgnoreCase("")) {
			eventValue = "WAPPR";
		}
		mbo.setValue("status", eventValue, 11L);
		app.getAppBean().save();
		return 1;
	}

	public void impExcelToContractLine() throws RemoteException, MXException {
		MboRemote mbo = app.getAppBean().getMbo();
		String status = mbo.getString("status");
		if (!"WAPPR".equals(status)) {
			throw new MXApplicationException("guide", "1066");
		}
	}
}
