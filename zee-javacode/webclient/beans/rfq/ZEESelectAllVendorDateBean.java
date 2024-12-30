package guide.webclient.beans.rfq;

import guide.app.rfq.UDRFQVendor;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

/**
 *@function:ZEE-RFQ-选择ALL VENDOR
 *@author:zj
 *@date:2024-09-11 13:12:19
 *@modify:
 */
public class ZEESelectAllVendorDateBean extends DataBean {
	
	public MboSetRemote getMboSetRemote() throws MXException, RemoteException {
		MboSetRemote msr = super.getMboSetRemote();
		MboRemote mbo = this.app.getAppBean().getMbo();
		String udcompany = mbo.getString("udcompany");
		String sql = "1=1";
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			sql = " company in (select company from udcomptaxcode where udcompany='ZEE') ";
		}
		msr.setWhere(sql);
		msr.reset();
		return msr;
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean table = app.getDataBean("quotations_quotations_vendorquotations_rfqvendorquo_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = table.getParent().getMbo();
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("RFQVENDOR");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				UDRFQVendor line = (UDRFQVendor) lineSet.add();
				line.setValue("vendor", mr.getString("company"), 2L);
			}
		}
		table.reloadTable();
		return 1;
	}
}