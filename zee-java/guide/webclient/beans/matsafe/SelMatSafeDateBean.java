package guide.webclient.beans.matsafe;

import java.rmi.RemoteException;

import psdi.util.MXException;
import psdi.webclient.system.beans.MultiselectDataBean;

public class SelMatSafeDateBean extends MultiselectDataBean {

	private boolean clearBean = false;

	// 刷新
	public int doRefresh() throws MXException, RemoteException {
		if (this.clearBean) {
			this.clearBean();
		}

		int savedCurrentRow = this.currentRow;
		this.reset();
		this.currentRow = savedCurrentRow;
		this.fireStructureChangedEvent();
		this.sessionContext.queueRefreshEvent();
		return 1;
	}
}
