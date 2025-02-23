package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

/**
 *@function:
 *@author:zj
 *@date:下午3:13:24
 *@modify:
 */
public class UDFldInvoiceHszj extends MboValueAdapter {

	public UDFldInvoiceHszj() {
		super();
	}

	public UDFldInvoiceHszj(MboValue mbv) {
		super(mbv);
	}
	
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		double udsaphszj = mbo.getDouble("udsaphszj");// 纸质发票含税总价
		double totalcost = 0.0D; //PO含税总价
		double udextracost = mbo.getDouble("udextracost"); //差额
		String ponum = mbo.getString("ponum");
		if (ponum!=null && !ponum.equalsIgnoreCase("")) {
			totalcost = mbo.getDouble("INVC_PO.totalcost");// PO含税总价
		}
//		mbo.setValue("udextracost", udsaphszj - totalcost, 11L);
		mbo.setValue("udsaplinecost", mbo.getDouble("udsaphszj") - mbo.getDouble("udsaptax"), 11L);
	}
}
