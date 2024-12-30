package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

/**
 *@function:
 *@author:zj
 *@date:下午3:39:12
 *@modify:
 */
public class UDFldInvoiceExtaxLineCost extends MboValueAdapter {

	public UDFldInvoiceExtaxLineCost() {
		super();
	}

	public UDFldInvoiceExtaxLineCost(MboValue mbv) {
		super(mbv);
	}
	
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		double totalcost = 0.0D; //PO含税总价
		String ponum = mbo.getString("ponum");
		if (ponum!=null && !ponum.equalsIgnoreCase("")) {
			totalcost = mbo.getDouble("INVC_PO.totalcost");// PO含税总价
		}
		
		mbo.setValue("udsaphszj", mbo.getDouble("udsaplinecost") + mbo.getDouble("udsaptax"), 11L);
//		mbo.setValue("udextracost", mbo.getDouble("udsaphszj") - totalcost, 11L);
	}
}
