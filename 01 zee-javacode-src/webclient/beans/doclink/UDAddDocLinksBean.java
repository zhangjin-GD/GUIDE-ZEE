package guide.webclient.beans.doclink;

import java.rmi.RemoteException;

import psdi.util.MXException;
import psdi.webclient.beans.doclinks.AddDocLinksBean;
import psdi.webclient.system.controller.WebClientEvent;

public class UDAddDocLinksBean extends AddDocLinksBean {

	@Override
	public synchronized void insert() throws MXException, RemoteException {
		super.insert();
		WebClientEvent event = clientSession.getCurrentEvent();
		String eventValue = event.getValue().toString();
		if (eventValue != null && !eventValue.isEmpty()) {
			this.setValue("doctype", eventValue, 11L);
		}
	}
}
