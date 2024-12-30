package guide.app.invoice;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 *@function:发票移交流程发起时校验
 *@author:zj
 *@date:2023-07-19 08:38:03
 *@modify:
 */
public class WFCheckInvoiceStartAction implements ActionCustomClass {

	@Override
	public void applyCustomAction(MboRemote mbo, Object[] arg1)
			throws MXException, RemoteException {
		MboSetRemote invoicelineSet = mbo.getMboSet("INVOICELINE");
		String status = mbo.getString("status");
		String udmatchstatus = mbo.getString("udmatchstatus");
		double udsaphszj = mbo.getDouble("udsaphszj");
		double udextracost = mbo.getDouble("udextracost");
		String udcosttype = mbo.getString("udcosttype");
		double udsaplinecost = mbo.getDouble("udsaplinecost");
		double udsaplinecost1 = mbo.getMboSet("INVOICELINE").sum("udsaplinecost");
		if (!status.equalsIgnoreCase("ENTERED") && !status.equalsIgnoreCase("BACK")) {
			Object params[] = { "The invoice status at process initiation must be ENTERED or BACK." };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		} else if (invoicelineSet.isEmpty() || invoicelineSet.count() == 0) {
			Object params[] = { "Invoiceline cannot be empty." };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		} else if (!udmatchstatus.equalsIgnoreCase("Matched")) {
			Object params[] = { "Invoice not matched, unable to initiate process." };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		} else if (udsaphszj == 0) {
			Object params[] = { "Invoice Total cannot be 0." };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		} else if (udextracost != 0 && (udcosttype == null || udcosttype.equalsIgnoreCase(""))) {
			Object params[] = { "Cost Type cannot be empty." };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		} else if (udsaplinecost!=udsaplinecost1) {
			Object params[] = { "The Pretax Total is incorrect. Please modify the Pretax Total in the invoiceline." };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		}
		
		//一个发票单据里,ponum和polinenum不能做多行
		List<String> lista = new ArrayList<String>();
		List<String> listb = new ArrayList<String>();
		if (!invoicelineSet.isEmpty() && invoicelineSet.count() > 0) {
			for (int i = 0; i < invoicelineSet.count(); i++) {
				MboRemote invoiceline = invoicelineSet.getMbo(i);
				String ponum = invoiceline.getString("ponum");
				int polinenum = invoiceline.getInt("polinenum");
				String num = ponum + polinenum;
				lista.add(num);
				listb.add(num);
			}
			
			HashSet h = new HashSet(lista);
			lista.clear();
			lista.addAll(h);
			
			if (lista.size() < listb.size()) {
				Object params[] = { "The current list has duplicate poline, please merge them." };
				throw new MXApplicationException("instantmessaging","tsdimexception", params);
			}
		}
		
//		//发票明细行里的不含税总价不能超过PO的不含税总价
//		double invoicelinecost = mbo.getMboSet("INVOICELINE").sum("linecost");
//		double polinecost = 0.0D;
//		MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE",MXServer.getMXServer().getSystemUserInfo());
//		polineSet.setWhere(" ponum='"+mbo.getString("ponum")+"' ");
//		polineSet.reset();
//		if (!polineSet.isEmpty() && polineSet.count() >0) {
//			polinecost = polineSet.sum("linecost");
//		}
//		polineSet.close();
//		
//		if (invoicelinecost > polinecost) {
//			Object params[] = { "Process initiation is not allowed, invoice amount cannot be greater than PO amount." };
//			throw new MXApplicationException("instantmessaging","tsdimexception", params);
//		}
		
		//每个INVOICELINE的不含税金额不能超过POLINE的不含税金额
		if (!invoicelineSet.isEmpty() && invoicelineSet.count() > 0) {
			for (int i = 0; i < invoicelineSet.count(); i++) {
				MboRemote invoiceline = invoicelineSet.getMbo(i);
				String ponum = invoiceline.getString("ponum");
				int polinenum = invoiceline.getInt("polinenum");
				double currentPolineOrderqty = 0.0D;
				MboSetRemote polineSet1 = MXServer.getMXServer().getMboSet("POLINE",MXServer.getMXServer().getSystemUserInfo());
				polineSet1.setWhere(" ponum='"+ponum+"' and polinenum='"+polinenum+"' ");
				polineSet1.reset();
				if (!polineSet1.isEmpty() && polineSet1.count() >0) {
					currentPolineOrderqty = polineSet1.getMbo(0).getDouble("orderqty");
				}
				polineSet1.close();
				
				List<String> list1 = new ArrayList<String>();
				MboSetRemote invoicelineSet1 = invoiceline.getMboSet("UDINVOICELINE");
				if (!invoicelineSet1.isEmpty() && invoicelineSet1.count() >0) {
					for (int j = 0; j < invoicelineSet1.count(); j++) {
						list1.add(invoicelineSet1.getMbo(j).getString("ponum"));
					}
				}
				HashSet h = new HashSet(list1);
				list1.clear();
				list1.addAll(h);
				
				double suminvoiceqty = invoicelineSet1.sum("invoiceqty");
				if (suminvoiceqty > currentPolineOrderqty) {
					Object params[] = { "Please check the invoiceline:"+invoiceline.getInt("invoicelinenum")+".\n ponum:"+ponum+",polinenum:"+polinenum+" already invoiced, related invoice system number:"+list1 };
					throw new MXApplicationException("instantmessaging","tsdimexception", params);
				}
				invoicelineSet1.close();
			}
		}
	}

}
