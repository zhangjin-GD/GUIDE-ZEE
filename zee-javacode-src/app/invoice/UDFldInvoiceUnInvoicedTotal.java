package guide.app.invoice;

import java.rmi.RemoteException;


import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 * @function:发票-自定义未开发票金额(以PO的不含税金额来计算)
 * @author:zj
 * @date:2023-09-26 14:27:41
 * @modify:
 */
public class UDFldInvoiceUnInvoicedTotal extends MboValueAdapter {

	public UDFldInvoiceUnInvoicedTotal() {
		super();
	}

	public UDFldInvoiceUnInvoicedTotal(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote invoice = this.getMboValue().getMbo();
		if (invoice.getString("ponum").equals("")) {
			getMboValue().setValueNull(11L);
		} else {
			double total = 0.0D;
			MboSetRemote invoicelineSet = MXServer.getMXServer().getMboSet("INVOICELINE",MXServer.getMXServer().getSystemUserInfo());
			invoicelineSet.setWhere(" ponum='" + invoice.getString("ponum")+ "' ");
			invoicelineSet.reset();
			total = invoicelineSet.sum("linecost");
			invoicelineSet.close();
			Double polinecost = invoice.getDouble("INVC_PO.pretaxtotal");
			getMboValue().setValue(polinecost - total, 11L);
		}
	}

}
