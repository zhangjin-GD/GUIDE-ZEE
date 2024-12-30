package guide.webclient.beans.pr;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.collections4.bag.SynchronizedSortedBag;

import guide.app.po.UDPO;
import guide.app.pr.UDPR;
import psdi.app.po.PORemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.po.PRLineBean;
import psdi.webclient.system.beans.DataBean;

public class UDPRlineItemDateBean extends DataBean {
	

	public synchronized int execute() throws MXException, RemoteException {
		DataBean prLine = app.getDataBean("prlines_prlines_table");
		Vector<MboRemote> vector = this.getSelection();
		if(vector.isEmpty()){
			throw new MXApplicationException("guide", "1234");
		}
		UDPR owner = (UDPR) prLine.getParent().getMbo();
		String description = owner.getString("description");
		
		if (owner.isNull("vendor")) {
			throw new MXApplicationException("guide", "1188");
		}
		String ponum = owner.addPOFromPR(description,vector);
		this.app.getAppBean().save();
		Object[] obj = { ponum};
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "guide", "1187", obj);
		return 1;
	}

}
