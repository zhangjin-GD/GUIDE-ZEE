package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.rfq.QuotationLineSet;
import psdi.app.rfq.QuotationLineSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDQuotationLineSet extends QuotationLineSet implements QuotationLineSetRemote{

	public UDQuotationLineSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDQuotationLine(ms);
	}
}
