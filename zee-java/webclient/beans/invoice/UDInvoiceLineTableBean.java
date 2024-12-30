package guide.webclient.beans.invoice;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;
import psdi.webclient.beans.invoice.InvoiceLineBean;
import psdi.webclient.system.beans.DataBean;

/**
 * @function:取消CREDIT NOTE，只能在状态为APPR/INPRG才可进行creditnote的启用与取消，CLOSE则不允许
 * @author:DJY
 * @date:2024-02-27 14:55:03
 * @modify:
 */
public class UDInvoiceLineTableBean extends InvoiceLineBean {
    static String isChange = "no";
    
    public void UDCANCEL() throws RemoteException, MXException {	
        MboRemote invoicembo = this.app.getAppBean().getMbo();
        if(!invoicembo.getString("udcompany").isEmpty() && invoicembo.getString("udcompany").equalsIgnoreCase("ZEE")){
        	String appName = invoicembo.getThisMboSet().getApp();
            if (appName == null) {
                return;
            }
            DataBean invoicelinebean = app.getDataBean("invlines_invoicelines");
            MboRemote invoiceline = invoicelinebean.getMbo(invoicelinebean.getCurrentRow());
            MboRemote po = invoiceline.getMboSet("PO").getMbo(0);
    		MboSetRemote matRecTransSet = po.getMboSet("PARENTMATRECTRANS");
    		//校验信息
    		int udcreditid = invoiceline.getInt("invoicelineid");
    		matRecTransSet.setWhere("udcreditcost is not null and udcreditid='"
    				+ udcreditid + "' ");
    		matRecTransSet.reset();

            if (matRecTransSet.isEmpty() || matRecTransSet.count() <= 0) {
                showMessageBox("Notice：There is no credit note exists, the cancel is invalid !");
                return;
            }

            double credit = invoiceline.getDouble("udcreditvalue");
            if (String.valueOf(credit) != null  && invoiceline.getDouble("udcreditvalue")==0.0) {
                showMessageBox("Notice：There is no credit note exists, the cancel is invalid !");
                return;
            }

            UserInfo userInfo = invoicembo.getUserInfo();
            int userInput = MXApplicationYesNoCancelException.getUserInput("UDCANCEL", MXServer.getMXServer(), userInfo);
            switch (userInput) {
                case 8:
                    handleCancel(invoiceline, po, credit, matRecTransSet);
                    invoiceline.getThisMboSet().save(); // 提交更改到数据库
                    break;
                case 16:
                    break;
                case -1:
                    throw new MXApplicationYesNoCancelException("UDCANCEL", "UDCANCEL", "UDCANCEL", new String[]{});
            }
            matRecTransSet.close();	
        }    
    }

    private void handleCancel(MboRemote invoiceLine, MboRemote poMbo, double credit, MboSetRemote matRecTransSet) throws MXException, RemoteException {
        if ("CLOSE".equalsIgnoreCase(poMbo.getString("status"))) {
            poMbo.setValue("status", "APPR", 11L);
            isChange = "yes";
        }

        if (String.valueOf(credit) != null  && !String.valueOf(credit).equalsIgnoreCase("0")) {
        	createCancelMatRecTransactions(invoiceLine, matRecTransSet, credit);
        }

        if ("yes".equalsIgnoreCase(isChange)) {
            MboSetRemote poSet = invoiceLine.getMboSet("PO");
            String poNum = poMbo.getString("ponum");
            String siteId = poMbo.getString("siteid");
            poSet.setWhere("ponum = '" + poNum + "' and siteid = '" + siteId + "'");
            poSet.reset();
            MboRemote updatedPo = poSet.getMbo(0);
            updatedPo.setValue("status", "CLOSE", 11L);
            poSet.save();
        }

        showMessageBox("Success: Cancel credit note  !");
        matRecTransSet.save();
    }
    
    private void createCancelMatRecTransactions(MboRemote invoiceLine, MboSetRemote matRecTransSet, double credit) throws MXException, RemoteException {
        MboRemote receiptTransaction = matRecTransSet.add(11L);
        setMatRecTransactionValues(receiptTransaction, invoiceLine, credit, "RECEIPT", 0.01, true);
        matRecTransSet.save();

        MboRemote returnTransaction = matRecTransSet.add(11L);
        setMatRecTransactionValues(returnTransaction, invoiceLine, credit, "RETURN", -0.01, false);
        matRecTransSet.save();
    }
    
    private void setMatRecTransactionValues(MboRemote matRec, MboRemote invoiceLine, double credit, String issueType, double quantity, boolean includeCost) throws MXException, RemoteException {
        matRec.setValue("itemnum", invoiceLine.getString("itemnum"), 2L);
        matRec.setValue("issuetype", issueType, 11L);
        matRec.setValue("status", "COMP", 11L);
        matRec.setValue("quantity", quantity, 2L);
        matRec.setValue("receiptquantity", quantity, 11L);
        matRec.setValue("udcreditid", invoiceLine.getInt("invoicelineid"), 11L);
        matRec.setValue("tolot", getLotnum(invoiceLine, matRec), 11L);

        if (includeCost) {
        	invoiceLine.setValue("udcreditvalue",  "0", 11L);
            matRec.setValue("currencylinecost", credit, 11L);
            matRec.setValue("unitcost", credit * 100, 2L);
            matRec.setValue("linecost", matRec.getDouble("unitcost") * matRec.getDouble("quantity"), 2L);
            matRec.setValue("loadedcost", matRec.getDouble("unitcost") * matRec.getDouble("quantity"), 11L);
            matRec.setValue("actualcost", matRec.getDouble("unitcost"), 11L);
            matRec.setValue("currencyunitcost", matRec.getDouble("unitcost"), 11L);
            matRec.setValue("tax1", getTaxRate(invoiceLine, matRec), 11L);
        } else {
            matRec.setValue("currencylinecost", 0, 11L);
            matRec.setValue("unitcost", 0, 2L);
            matRec.setValue("linecost", 0, 2L);
            matRec.setValue("actualcost", 0, 11L);
            matRec.setValue("currencyunitcost", 0, 11L);
            matRec.setValue("tax1", 0, 11L);
        }
    }

    private double getTaxRate(MboRemote lineMbo, MboRemote matRec) throws MXException, RemoteException {
        double taxRate = 0.0D;
        String tax1Code = lineMbo.getString("tax1code");
        MboSetRemote taxSet = MXServer.getMXServer().getMboSet("TAX", MXServer.getMXServer().getSystemUserInfo());
        taxSet.setWhere("taxcode='" + tax1Code + "'");
        taxSet.reset();
        if (!taxSet.isEmpty() && taxSet.count() > 0) {
            taxRate = taxSet.getMbo(0).getDouble("taxrate");
        }
        taxSet.close();
        return taxRate * 0.01 * matRec.getDouble("linecost");
    }

    private String getLotnum(MboRemote lineMbo, MboRemote matRec) throws MXException, RemoteException {
        String itemnum = lineMbo.getString("itemnum");
        String location = matRec.getString("tostoreloc");
        MboSetRemote invBalancesSet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
        invBalancesSet.setWhere("itemnum='" + itemnum + "' and location='" + location + "' and curbal>0");
        invBalancesSet.reset();
        String lotnum = "";
        if (!invBalancesSet.isEmpty() && invBalancesSet.count() > 0) {
            lotnum = invBalancesSet.getMbo(0).getString("lotnum");
        }
        invBalancesSet.close();
        return lotnum;
    }

    private void showMessageBox(String message) throws RemoteException, MXException {
        clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", message, 1);
    }
}
