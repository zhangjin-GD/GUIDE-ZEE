package guide.webclient.beans.rfq;

import java.rmi.RemoteException;

import psdi.app.rfq.RFQVendorRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.rfq.QuotationsVendorTableBean;

public class UDQuotationsVendorTableBean extends QuotationsVendorTableBean {

	public int udcreatecont() throws MXException, RemoteException {
		RFQVendorRemote rfqRemote = (RFQVendorRemote) this.getMbo();
		if (rfqRemote != null) {
			MboSetRemote contSet = rfqRemote.getMboSet("UDCONTRACT");
			if (!contSet.isEmpty() && contSet.count() > 0) {
				MboRemote cont = contSet.getMbo(0);
				String gconnum = cont.getString("gconnum");
				Object[] obj = { gconnum };
				throw new MXApplicationException("guide", "1114", obj);
			}
			rfqRemote.checkingBeforeCreatePOCont(false);
			return 2;
		} else {
			throw new MXApplicationException("contract", "noVendor");
		}
	}
	
	/**
	 * ZEE - RFQVENDOR:合并已授予供应商line到已存在PO，初始空集合校验
	 * DJY 32-43
	 * 2024-10-28 10:02
	 **/
	public void UDCOMBINETOEXPO() throws MXException, RemoteException {
		RFQVendorRemote rfqRemote = (RFQVendorRemote) this.getMbo();
		if (rfqRemote == null) {
			Object str0[] = { " No vendors here! "};
			throw new MXApplicationException("instantmessaging", "tsdimexception",str0);
		}		
	}
	
}
