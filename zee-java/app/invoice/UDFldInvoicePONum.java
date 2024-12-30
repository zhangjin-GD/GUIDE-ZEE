package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.app.common.TaxUtility;
import psdi.app.invoice.Invoice;
import psdi.app.po.FldPONum;
import psdi.mbo.*;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

/**
 * 
 * @function:
 * @author:YS
 * @modify:
 */
public class UDFldInvoicePONum extends FldPONum
{

    public UDFldInvoicePONum(MboValue mbv)
        throws MXException, RemoteException
    {
        super(mbv);
        setLookupKeyMapInOrder(new String[] {
            "positeid", "ponum"
        }, new String[] {
            "siteid", "ponum"
        });
    }

    public void validate()
        throws MXException, RemoteException
    {
        if(getMboValue().isNull())
            return;
        try
        {
            super.validate();
        }
        catch(MXApplicationException ex)
        {
            if(ex.equals("po", "InvalidPONum"))
                throw new MXApplicationException("inventory", "invalidpo");
            else
                throw ex;
        }
        Invoice invoiceMbo = (Invoice)getMboValue().getMbo();
        String type = getMboValue("documenttype").getString();
        if(getTranslator().toInternalString("INVTYPE", type, invoiceMbo).equalsIgnoreCase("CONSIGNMENT"))
            throw new MXApplicationException("invoice", "cannotusepo");
        String ponum = getMboValue().getString();
        if(invoiceMbo.isNull("positeid"))
        {
            Object params[] = {
                ponum, invoiceMbo.getString("invoicenum")
            };
            throw new MXApplicationException("invoice", "InvoicePOSiteIDRequired", params);
        }
        String poWappr = getTranslator().toExternalList("POSTATUS", "WAPPR", invoiceMbo);//添加的
        String poAppr = getTranslator().toExternalList("POSTATUS", "APPR", invoiceMbo);
        String poInprg = getTranslator().toExternalList("POSTATUS", "INPRG", invoiceMbo);
        String poClose = getTranslator().toExternalList("POSTATUS", "CLOSE", invoiceMbo);
        String statuses = (new StringBuilder()).append(poAppr).append(", ").append(poInprg).append(", ").append(poClose).append(", ").append(poWappr).toString();//添加了poWappr
        String query = (new StringBuilder()).append("status in (").append(statuses).append(") and ponum = :1 and siteid = :2").toString();
        SqlFormat sqfNew = new SqlFormat(invoiceMbo.getUserInfo(), query);
        sqfNew.setObject(1, "INVOICELINE", "ponum", ponum);
        sqfNew.setObject(2, "INVOICELINE", "positeid", getMboValue("positeid").getString());
        MboSetRemote poSet = getMboValue().getMbo().getMboSet("$invoicelinepo", "PO", sqfNew.format());
        if(poSet.isEmpty())
            throw new MXApplicationException("po", "InvalidPONum");
        MboRemote po = poSet.getMbo(0);
        String invoiceOrgId = getMboValue("orgid").getString();
        String poOrgId = po.getString("orgid");
        if(!invoiceOrgId.equals(poOrgId))
        {
            getMboValue("positeid").setValueNull(2L);
            Object params[] = {
                getMboValue("ORGID").getColumnTitle(), invoiceOrgId
            };
            throw new MXApplicationException("system", "OrgIdCannotBeSet", params);
        }
        if(po.getBoolean("internal"))
            throw new MXApplicationException("po", "InvalidPONum");
        String poStatus = po.getString("status");
        if(!getTranslator().toInternalString("INVTYPE", invoiceMbo.getString("documenttype")).equals("SCHED") && (getTranslator().toInternalString("POSTATUS", poStatus, invoiceMbo).equalsIgnoreCase("CAN")))
        {
            poSet.reset();
            Object params[] = {
                ponum, poAppr, poInprg, poClose
            };
            throw new MXApplicationException("invoice", "InvalidPOStatus", params);
        }
        String invoiceVendor = invoiceMbo.getString("vendor");
        String poVendor = po.getString("vendor");
        if(invoiceVendor != null && !invoiceVendor.equals(""))
        {
            query = (new StringBuilder()).append("status in (").append(poAppr).append(",").append(poInprg).append(",").append(poClose).append(")").toString();
            query = (new StringBuilder()).append(query).append(" and :1 in ((select company from companies where company = :2 and orgid = :3) union (select company from companies where parentcompany = :2 and orgid = :3))").toString();
            SqlFormat sqf = new SqlFormat(invoiceMbo.getUserInfo(), query);
            sqf.setObject(1, "PO", "vendor", poVendor);
            sqf.setObject(2, "INVOICE", "vendor", invoiceVendor);
            sqf.setObject(3, "INVOICE", "orgid", invoiceMbo.getString("orgid"));
            if(invoiceMbo.getMboSet("$potoinvoice", "PO", sqf.format()).isEmpty())
            {
                poSet.reset();
                Object params[] = {
                    ponum
                };
                throw new MXApplicationException("invoice", "WrongPOVendor", params);
            }
        }
    }

    public void action()
        throws MXException, RemoteException
    {
        Invoice invoice = (Invoice)getMboValue().getMbo();
        MboRemote company = null;
        if(getMboValue().isNull())
        {
            if(!getMboValue("vendor").isNull())
            {
                company = invoice.getMboSet("COMPANIES").getMbo(0);
                getMboValue("currencycode").setValue(company.getString("currencycode"), 11L);
                getMboValue().getMbo().setFieldFlag("vendor", 7L, false);
            }
            getMboValue("uninvoicedtotal").setValueNull(11L);
            invoice.setValueNull("contractrefnum", 2L);
            invoice.setFieldFlag("contractrefnum", 7L, false);
            invoice.setValueNull("positeid", 2L);
            return;
        }
        MboSetRemote invoicePOSet = invoice.getMboSet("PO");
        MboRemote invoicePO = invoicePOSet.getMbo(0);
        if(getMboValue("vendor").isNull())
            invoice.setValue("vendor", invoicePO.getString("vendor"), 2L);
        invoice.setFieldFlag("vendor", 7L, true);
        invoice.setValue("paymentterms", invoicePO.getString("paymentterms"), 2L);
        invoice.setValue("currencycode", invoicePO.getString("currencycode"), 2L);
        TaxUtility taxUtility = TaxUtility.getTaxUtility();
        taxUtility.setTaxattrValue(invoice, "INCLUSIVE", invoicePO, 11L);
        invoice.setValue("contractrefnum", invoicePO.getString("contractrefnum"), 11L);
        invoice.setValue("contractrefid", invoicePO.getString("contractrefid"), 11L);
        invoice.setValue("contractrefrev", invoicePO.getString("contractrefrev"), 11L);
        invoice.setFieldFlag("contractrefnum", 7L, true);
        if(invoicePO.getBoolean("buyahead"))
        {
            invoice.setValue("exchangerate", invoicePO.getString("exchangerate"), 2L);
            invoice.setValue("exchangerate2", invoicePO.getString("exchangerate2"), 2L);
            invoice.setValue("exchangedate", invoicePO.getString("exchangedate"), 2L);
        }
        invoice.setValue("uninvoicedtotal", invoice.calculateUnInvoicedTotal(), 11L);
        /**
         * ZEE
         * 2023-07-19 20:26:31
         */
//        MboRemote mbo = getMboValue().getMbo();
//        String udcompany = mbo.getString("udcompany");
//        if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
//            double totalcost = 0.0D; //PO含税总价
//            String ponum = mbo.getString("ponum");
//            if (ponum!=null && !ponum.equalsIgnoreCase("")) {
//            	totalcost = mbo.getDouble("INVC_PO.totalcost");// PO含税总价
//            }
//            mbo.setValue("udextracost", mbo.getDouble("udsaphszj") - totalcost, 11L);
//            
//            //校验：当前PO是否已做发票
//            double suminvoicelinecost = 0.0D;
//            double sumpolinecost = 0.0D;
//			MboSetRemote invoicelineSet = MXServer.getMXServer().getMboSet("INVOICELINE",MXServer.getMXServer().getSystemUserInfo());
//			invoicelineSet.setWhere(" ponum='"+ponum+"' ");
//			invoicelineSet.reset();
////			if (invoicelineSet!=null && !invoicelineSet.isEmpty() && invoicelineSet.count() >0) {
//				suminvoicelinecost = invoicelineSet.sum("linecost");
////			}
//			invoicelineSet.close();
//			
//			MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE",MXServer.getMXServer().getSystemUserInfo());
//			polineSet.setWhere(" ponum='"+ponum+"' ");
//			polineSet.reset();
////			if (!polineSet.isEmpty() && polineSet.count() >0) {
//				sumpolinecost = polineSet.sum("linecost");
////			}
//			polineSet.close();
//			
//			if (sumpolinecost <= suminvoicelinecost) {
//				Object params[] = { "This PO has already been invoiced and cannot be selected." };
//				throw new MXApplicationException("instantmessaging","tsdimexception", params);
//			}
//		}
    }

    public boolean hasList()
    {
        return true;
    }

    public MboSetRemote getList()
        throws MXException, RemoteException
    {
        Mbo mbo = getMboValue().getMbo();
        String udcompany = mbo.getString("udcompany");//添加公司过滤
        System.out.println("udcompany----------------"+udcompany);
        //String wappr = getTranslator().toExternalList("POSTATUS", "WAPPR", mbo);删除
        String cancel = getTranslator().toExternalList("POSTATUS", "CAN", mbo);
        String revise = getTranslator().toExternalList("POSTATUS", "REVISE", mbo);
        String pndrev = getTranslator().toExternalList("POSTATUS", "PNDREV", mbo);
        String hold = getTranslator().toExternalList("POSTATUS", "HOLD", mbo);
        String consingment = getTranslator().toExternalList("POTYPE", "CONSIGNMENT", mbo);
        String query = (new StringBuilder()).append("potype not in (").append(consingment).append(") and status not in (").append(cancel).append(",").append(revise).append(",").append(pndrev).append(",").append(hold).append(") and udcompany='"+udcompany+"' and internal = :no and orgid = :2").toString();
        System.out.println("query----------------"+query);
        if(!getMboValue("vendor").isNull())
        {
            String invoiceVendor = mbo.getString("vendor");
            query = (new StringBuilder()).append(query).append(" and vendor in ((select company from companies where company = :1 and orgid = :2) union (select company from companies where parentcompany = :1 and orgid = :2))").toString();
            SqlFormat sqf = new SqlFormat(mbo.getUserInfo(), query);
            sqf.setObject(1, "INVOICE", "vendor", invoiceVendor);
            if(!mbo.isZombie())
                sqf.setObject(2, "INVOICE", "orgid", mbo.getString("orgid"));
            else
                sqf.setObject(2, "INVOICE", "orgid", mbo.getProfile().getInsertOrg());
            setListCriteria(sqf.format());
        } else
        {
            SqlFormat sqf = new SqlFormat(mbo.getUserInfo(), query);
            if(!mbo.isZombie())
                sqf.setObject(2, "INVOICE", "orgid", mbo.getString("orgid"));
            else
                sqf.setObject(2, "INVOICE", "orgid", mbo.getProfile().getInsertOrg());
            setListCriteria(sqf.format());
        }
        return super.getList();
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: D:\jiabao\lib\businessobjects.jar
	Total time: 19 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/