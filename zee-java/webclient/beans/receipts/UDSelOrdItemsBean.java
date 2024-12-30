package guide.webclient.beans.receipts;

import java.rmi.RemoteException;

import psdi.util.MXException;
import psdi.webclient.beans.receipts.SelOrdItemsBean;
import psdi.webclient.system.beans.DataBean;

public class UDSelOrdItemsBean extends SelOrdItemsBean {

	public int execute() throws MXException, RemoteException {
		this.save();
		this.structureChangedEvent(this.parent);
		this.fireChildChangedEvent();
		DataBean fix = this.app.getDataBean("fixreceipts_table");

		if (fix != null) {
			fix.refreshTable();
		}

		this.sessionContext.queueRefreshEvent();
		return 1;
	}
}
