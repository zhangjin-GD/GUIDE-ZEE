package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.app.invoice.InvoiceLine;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class UdInvoiceLine extends InvoiceLine {

	public UdInvoiceLine(MboSet ms) throws RemoteException {
		super(ms);
	}
	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		setValue("gldebitacct", "COSCO",11L);
	}
}
