package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.app.invoice.InvoiceLineSet;
import psdi.app.invoice.InvoiceSet;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UdInvoiceLineSet extends InvoiceLineSet {
	
	public UdInvoiceLineSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}
	
	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UdInvoiceLine(ms);
	}
	
}
