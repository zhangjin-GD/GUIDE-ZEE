package guide.webclient.beans.invoice;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

/**
 *@function:初始化发票中间表创建发票
 *@author:zj
 *@date:2023-07-10 17:16:14
 *@modify:
 */
public class SelectInvoiceInitDataBean extends DataBean {
	
	public MboSetRemote getMboSetRemote() throws MXException, RemoteException {
		String sql = "";
		MboSetRemote invoiceInitSet = super.getMboSetRemote();
		String personid = clientSession.getUserInfo().getPersonId();
		sql = " status not in ('Created','Cancel') ";
		invoiceInitSet.setWhere(sql);
		invoiceInitSet.reset();
		return invoiceInitSet;
	}
	  
	public int execute() throws MXException, RemoteException {
		if (this.app.onListTab()) {
			MboSetRemote invoiceDraftSet = getMboSet();
			Vector invoiceDraftVec = invoiceDraftSet.getSelection();
			int size = invoiceDraftVec.size();
			invoiceDraftSet.save();
			if (size==0) {
				return 2;
			}
			MboRemote invoiceDraft = null;
			String key = "";
			double amount = 0.0D;
			String supplier = "";
			String invoicenum = "";
			Date invoicedate = null;
			String ponum = "";
			String vendor = "";
			int udinvoiceinitid = 0;
			
			List<String> list = new ArrayList<String>();
			List<String> list0 = new ArrayList<String>();
			List<String> list1 = new ArrayList<String>();
			List<String> list2 = new ArrayList<String>();
			List<String> list3 = new ArrayList<String>();
			List<Date> list4 = new ArrayList<Date>();
			List<String> list5 = new ArrayList<String>();
			List<String> list6 = new ArrayList<String>();
			
			List<String> list7 = new ArrayList<String>();
			List<String> list8 = new ArrayList<String>();
			List<String> list9 = new ArrayList<String>();
			List<Date> list10 = new ArrayList<Date>();
			List<String> list11 = new ArrayList<String>();
			List<String> list12 = new ArrayList<String>();
			List<String> list13 = new ArrayList<String>();
			
			/**
			 * 勾选的数据里内部校验
			 */
			for (int i = 0; i < size; i++) {
				invoiceDraft = (MboRemote) invoiceDraftVec.elementAt(i);
				
				key = invoiceDraft.getString("key");
				if (list0.contains(key)) {
					list1.add(key); //已重复的key
				} else {
					list0.add(key);
				}
				
				invoicenum = invoiceDraft.getString("invoicenum");
				if (list2.contains(key)) {
					list3.add(invoicenum); //已重复的invoicenum
				} else {
					list2.add(key);
				}
			}
			if (list1.size()>0) {
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "There are duplicate keys!\nDuplicate keys："+list1, 1);
				return 2;
			}
			if (list3.size()>0) {
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "There are duplicate invoicenum!\nDuplicate invoicenum："+list3, 1);
				return 2;
			}
			
			/**
			 * 勾与数据库校验
			 */
			for (int i = 0; i < size; i++) {
				invoiceDraft = (MboRemote) invoiceDraftVec.elementAt(i);
				
				key = invoiceDraft.getString("key");
				MboSetRemote invoiceSet1 = MXServer.getMXServer().getMboSet("INVOICE", MXServer.getMXServer().getSystemUserInfo());
				invoiceSet1.setWhere(" udkey='"+key+"' ");
				invoiceSet1.reset();
				if (!invoiceSet1.isEmpty() && invoiceSet1.count() > 0) {
					list7.add(key);
				}
				
				amount = invoiceDraft.getDouble("amount");
				
				supplier = invoiceDraft.getString("supplier");
				vendor = getVendor(supplier);
				if (vendor==null || vendor.equals("")) {
					list8.add(supplier);
				}
				
				invoicenum = invoiceDraft.getString("invoicenum");
				MboSetRemote invoiceSet3 = MXServer.getMXServer().getMboSet("INVOICE", MXServer.getMXServer().getSystemUserInfo());
				invoiceSet3.setWhere(" vendorinvoicenum='"+invoicenum+"' ");
				invoiceSet3.reset();
				if (!invoiceSet3.isEmpty() && invoiceSet3.count() > 0) {
					list9.add(invoicenum);
				}
				
				invoicedate = invoiceDraft.getDate("invoicedate");
				if (invoicedate.after(MXServer.getMXServer().getDate())) {
					list10.add(invoicedate);
				}
				
				ponum = invoiceDraft.getString("ponum");
				if (ponum!=null && !ponum.equalsIgnoreCase("")) {
					MboSetRemote poSet = MXServer.getMXServer().getMboSet("PO", MXServer.getMXServer().getSystemUserInfo());
					poSet.setWhere(" ponum='"+ponum+"' ");
					poSet.reset();
					if (poSet.isEmpty() || poSet.count() == 0) {
						list11.add(ponum);
					}
					poSet.close();
					
					MboSetRemote poSet1 = MXServer.getMXServer().getMboSet("PO", MXServer.getMXServer().getSystemUserInfo());
					poSet1.setWhere(" ponum='"+ponum+"' and status='CAN' ");
					poSet1.reset();
					if (!poSet1.isEmpty() && poSet1.count() > 0) {
						list12.add(ponum);
					}
					poSet1.close();
				}
			}
			if (list7.size()>0) {
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Key already exists in the database!\nExist key："+list7, 1);
				return 2;
			}
			if (list8.size()>0) {
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Supplier does not exist!\nSupplier："+list8, 1);
				return 2;
			}
			if (list9.size()>0) {
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Invoicenum already exists in the database!\nExist invoicenum："+list9, 1);
				return 2;
			}
			if (list10.size()>0) {
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "The invoice date cannot exceed the current date!\nInvoice date："+list10, 1);
				return 2;
			}
			if (list11.size()>0) {
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Ponum does not exist!\nPonum："+list11, 1);
				return 2;
			}
			if (list12.size()>0) {
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "PO cancelled!\nPonum："+list12, 1);
				return 2;
			}
			
			/**
			 * 校验无误,生成发票
			 */
			MboSetRemote invoiceSet = MXServer.getMXServer().getMboSet("INVOICE", MXServer.getMXServer().getSystemUserInfo());
			invoiceSet.setWhere(" 1=2 ");
			invoiceSet.reset();
			for (int i = 0; i < size; i++) {
				invoiceDraft = (MboRemote) invoiceDraftVec.elementAt(i);
				
				MboRemote invoice = invoiceSet.add();
				addInvoice(invoice,invoiceDraft);

				MboSetRemote poSet = MXServer.getMXServer().getMboSet("PO",MXServer.getMXServer().getSystemUserInfo());
				poSet.setWhere(" ponum='" + ponum + "' and status!='CAN' ");
				poSet.reset();
				if (!poSet.isEmpty() && poSet.count() > 0) {
					invoice.setValue("vendor", poSet.getMbo(0).getString("vendor"), 2L);
					
					MboSetRemote polineSet = poSet.getMbo(0).getMboSet("POLINE");
					
					MboSetRemote invoicelineSet = invoice.getMboSet("INVOICELINE");
					
					for (int j = 0; j < polineSet.count(); j++) {
						MboRemote poline = polineSet.getMbo(j);
						MboRemote invoiceline = invoicelineSet.add();
						addInvoiceLine(invoiceline,poline);
					}
					
				}
				poSet.close();

				list.add(invoice.getString("invoicenum"));
				
				ChangeStatusForInvoiceInit(invoiceDraft);
			}
			
			invoiceSet.save();
			invoiceSet.close();
			
			clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Invoice created successfully!\nInvoice serial number："+list, 1);
			this.app.getAppBean().save();
            this.app.getAppBean().reloadTable();
            this.app.getAppBean().refreshTable();

			return 2;
		}
		return 2;
	}
	
	private void addInvoice(MboRemote invoice,MboRemote invoiceDraft) throws MXException,RemoteException {
		String key = invoiceDraft.getString("key");
		double amount = invoiceDraft.getDouble("amount");
		String supplier = invoiceDraft.getString("supplier");
		String vendor = getVendor(supplier);
		String invoicenum = invoiceDraft.getString("invoicenum");
		Date invoicedate = invoiceDraft.getDate("invoicedate");
		String ponum = invoiceDraft.getString("ponum");
		int udinvoiceinitid = invoiceDraft.getInt("udinvoiceinitid");
		
		invoice.setValue("enterby", clientSession.getUserInfo().getPersonId(), 11L);
		invoice.setValue("documenttype", "INVOICE", 11L);
		invoice.setValue("exchangerate", "1", 2L);
		invoice.setValue("langcode", "EN", 2L);
		invoice.setValue("glpostdate", MXServer.getMXServer().getDate(), 11L);
		invoice.setValue("udcurrency", getCurrency(ponum), 11L);
		invoice.setValue("positeid", "CSPL", 11L);
		invoice.setValue("description", "Supplier:"+supplier+",InvoiceNum:"+invoicenum, 11L);
		invoice.setValue("udkey", key, 11L);
		invoice.setValue("vendor", vendor, 11L);
		invoice.setValue("vendorinvoicenum", invoicenum, 11L);
		invoice.setValue("invoicedate", invoicedate, 2L);
		invoice.setValue("udsaphszj", amount, 2L);
		invoice.setValue("ponum", ponum, 2L);
		invoice.setValue("udinvoiceinitid", udinvoiceinitid, 11L);
//		invoice.setValue("udextracost", getExtraCost(amount,ponum,invoice), 11L);
		invoice.setValue("udmatchstatus", getMatchstatus(ponum,amount), 11L);
	}
	
	public String getVendor(String supplier) throws MXException,RemoteException {
		String vendor = "";
		MboSetRemote companiesSet = MXServer.getMXServer().getMboSet("COMPANIES", MXServer.getMXServer().getSystemUserInfo());
		companiesSet.setWhere(" name='"+supplier+"' ");
		companiesSet.reset();
		if (!companiesSet.isEmpty() && companiesSet.count() > 0) {
			vendor = companiesSet.getMbo(0).getString("company");
		}
		companiesSet.close();
		return vendor;
	}
	
	public String getCurrency(String ponum) throws MXException,RemoteException {
		String currency = "";
		MboSetRemote poSet = MXServer.getMXServer().getMboSet("PO", MXServer.getMXServer().getSystemUserInfo());
		poSet.setWhere(" ponum='"+ponum+"' ");
		poSet.reset();
		if (!poSet.isEmpty() && poSet.count() > 0) {
			currency = poSet.getMbo(0).getString("udcurrency");
		}
		poSet.close();
		return currency;
	}
	
	public String getMatchstatus(String ponum,double amount) throws MXException,RemoteException {
		String udmatchstatus = "";
		MboSetRemote poSet = MXServer.getMXServer().getMboSet("PO", MXServer.getMXServer().getSystemUserInfo());
		poSet.setWhere(" ponum='"+ponum+"' ");
		poSet.reset();
		if (!poSet.isEmpty() && poSet.count() > 0) {
			double linecost = poSet.getMbo(0).getMboSet("POLINE").sum("linecost");
			if (linecost == amount) {
				udmatchstatus = "Matched";
			}
		}
		poSet.close();
		return udmatchstatus;
	}
	
	private void addInvoiceLine(MboRemote invoiceline,MboRemote poline) throws MXException,RemoteException {
		invoiceline.setValue("linetype", "ITEM", 11L);
		invoiceline.setValue("itemnum",poline.getString("itemnum"), 2L);
		invoiceline.setValue("description",poline.getString("description"), 11L);
		invoiceline.setValue("receiptreqd", "1", 11L);
		invoiceline.setValue("invoiceunit",poline.getString("orderunit"), 11L);
		invoiceline.setValue("conversion", "1", 11L);
		invoiceline.setValue("ponum",poline.getString("ponum"), 11L);
		invoiceline.setValue("polinenum",poline.getString("polinenum"), 11L);
		invoiceline.setValue("porevisionnum",poline.getString("revisionnum"), 11L);
		invoiceline.setValue("positeid",poline.getString("siteid"), 11L);
		invoiceline.setValue("invoiceqty",poline.getDouble("orderqty"), 11L);
		invoiceline.setValue("tax1code",poline.getString("tax1code"), 11L);
		invoiceline.setValue("tax1",poline.getDouble("tax1"), 11L);
		invoiceline.setValue("unitcost",poline.getDouble("unitcost"), 11L);
		invoiceline.setValue("linecost",poline.getDouble("linecost"), 11L);
		invoiceline.setValue("udtotalprice",poline.getDouble("udtotalprice"), 11L);
		invoiceline.setValue("udtotalcost",poline.getDouble("udtotalcost"), 11L);
		invoiceline.setValue("udsaplinecost",poline.getDouble("linecost"), 11L);
	}
	
	private double getExtraCost(double amount, String ponum, MboRemote invoice) throws MXException,RemoteException {
		double extracost = amount;
		if (ponum!=null && !ponum.equalsIgnoreCase("")) {
			double totalcost = invoice.getDouble("INVC_PO.totalcost");
			extracost = amount - totalcost;
		}
		return extracost;
	}
	
	private void ChangeStatusForInvoiceInit(MboRemote selectMbo) throws MXException,RemoteException {
		MboSetRemote udinvoiceinitSet = MXServer.getMXServer().getMboSet("UDINVOICEINIT", MXServer.getMXServer().getSystemUserInfo());
		udinvoiceinitSet.setWhere(" key='"+selectMbo.getString("key")+"' ");
		udinvoiceinitSet.reset();
		if (!udinvoiceinitSet.isEmpty() && udinvoiceinitSet.count() > 0) {
			MboRemote udinvoiceinit = udinvoiceinitSet.getMbo(0);
			udinvoiceinit.setValue("status", "Created", 11L);
		}
		udinvoiceinitSet.save();
		udinvoiceinitSet.close();
	}
}
