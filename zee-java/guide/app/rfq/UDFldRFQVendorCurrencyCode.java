package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.rfq.FldRFQVendorCurrencyCode;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
/**
 *@function:解决：当PR.VENDOR有值,在RFQ做PRTORFQ时,RFQVENDOR.udtax1code为空,报错
 *@author:zj
 *@date:2024-09-14 14:18:07
 *@modify:
 */
public class UDFldRFQVendorCurrencyCode extends FldRFQVendorCurrencyCode {

	public UDFldRFQVendorCurrencyCode(MboValue mbv) throws MXException {
		super(mbv);
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo =getMboValue().getMbo();
		mbo.setValue("udtax1code", "1L", 11L);
	}
	
}
