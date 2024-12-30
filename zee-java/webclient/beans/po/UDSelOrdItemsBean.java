package guide.webclient.beans.po;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.receipts.SelOrdItemsBean;
/**
 *@function:接收-选择已订购项目-只选择POLINE中STATUS是CONFIRMED
 *@author:zj
 *@date:2024-06-04 13:00:04
 *@modify:
 */
public class UDSelOrdItemsBean extends SelOrdItemsBean {
	
	public int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		String udcompany = mbo.getString("udcompany");
		if (udcompany != null && udcompany.equalsIgnoreCase("ZEE") && getId().equalsIgnoreCase("selorditem")) {
			Vector<MboRemote> vec = getSelection();
			if (vec.size() > 0) {
				for (int i = 0; vec.size() > i; i++) {
					MboRemote receiptinput = (MboRemote) vec.elementAt(i);
					String udstatus = receiptinput.getString("POLINE.udstatus");
					if (udstatus != null && (!udstatus.equalsIgnoreCase("CONFIRMED") && !udstatus.equalsIgnoreCase("PART"))) {
						Object params[] = { "PO Line status must be CONFIRMED or PART !" };
						throw new MXApplicationException("instantmessaging", "tsdimexception",params);
					}
				}
			}
		}
		return super.execute();
	}
}
