package guide.webclient.beans.invoice;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

/**
 * @function:ZEE-credit note
 * @author:djy
 * @modify:
 */
public class UDCreaditNoteDataBean extends DataBean {

	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote mbo = this.app.getAppBean().getMbo();
		if(!mbo.getString("udcompany").isEmpty() && mbo.getString("udcompany").equalsIgnoreCase("ZEE")){
		//校验信息
		if (mbo.getMboSet("INVOICELINE").isEmpty() || mbo.getMboSet("INVOICELINE").count() == 0) {
			Object params[] = { "Note: No Invoice Lines." };
			throw new MXApplicationException("instantmessaging", "tsdimexception",params);
		}	
		DataBean db = app.getDataBean("invlines_invoicelines");
		MboRemote linembo = db.getMbo(db.getCurrentRow());
		if (linembo == null) {
			return;
		}
		if(String.valueOf(linembo.getDouble("udcreditvalue")) != null && linembo.getDouble("udcreditvalue")!=0.0){
			Object params[] = { "Note: You should cancel first before the twice credit note! " };
			throw new MXApplicationException("instantmessaging", "tsdimexception",params);
		}
		
		String itemnum = linembo.getString("itemnum");
		String ponum = linembo.getString("ponum");
		Integer polinenum = linembo.getInt("polinenum");
		String location = "";
		String binnum = "";
		String invoicenum = linembo.getString("invoicenum");
		limitCredit(invoicenum,itemnum);

		MboSetRemote matrectransviewSet = MXServer.getMXServer().getMboSet("MATRECTRANS", MXServer.getMXServer().getSystemUserInfo());
		matrectransviewSet.setWhere(" itemnum='"+itemnum+"' and ponum='"+ponum+"' and polinenum='"+polinenum+"' and issuetype='RECEIPT' ");
		matrectransviewSet.reset();

		if (!matrectransviewSet.isEmpty() && matrectransviewSet.count() > 0) {
			MboRemote matrectransview = matrectransviewSet.getMbo(0);
			location = matrectransview.getString("tostoreloc");
			binnum = matrectransview.getString("tobin");
		}
		matrectransviewSet.close();

		nopricelimit(itemnum, location);
		}
	}

	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if(!mbo.getString("udcompany").isEmpty() && mbo.getString("udcompany").equalsIgnoreCase("ZEE")){
			//校验信息
			if (getString("udcredit") != null && getString("udcredit").equalsIgnoreCase("")) {
				Object params[] = { "Credit must be entered!" };
				throw new MXApplicationException("instantmessaging", "tsdimexception", params);
			}
			DataBean db = app.getDataBean("invlines_invoicelines");
			MboRemote linembo = db.getMbo(db.getCurrentRow());
			if (linembo == null) {
				return 1;
			}

			double credit = Double.parseDouble(getString("udcredit").replace(",", ""));
			MboRemote po = linembo.getMboSet("PO").getMbo(0);
			MboSetRemote matrectransSet = po.getMboSet("PARENTMATRECTRANS");
			
			handleCredit(linembo, po, credit, matrectransSet);
			linembo.getThisMboSet().save(); // 提交更改到数据库	
		}	
		return 1;
	}
	
	private void limitCredit(String invoiceNum, String itemNum) throws RemoteException, MXException{
		/**
		 * 对于已接收的物资，发票APPR后，只要该物资有任何的issue、return记录
		 * （matuse.transdate>invoice.changedate，且sum（issue和return）！=0），则不允许做credit note
		 */	
		MboSetRemote invoicestatusSet = MXServer.getMXServer().getMboSet("INVOICESTATUS", MXServer.getMXServer().getSystemUserInfo());
		invoicestatusSet.setWhere("invoicenum = '" + invoiceNum+ "' and status = 'APPR'");
		invoicestatusSet.reset();
		if(!invoicestatusSet.isEmpty() && invoicestatusSet.count() > 0){
		String changedate = invoicestatusSet.getMbo(0).getString("changedate");
		MboSetRemote matusetransSet = MXServer.getMXServer().getMboSet("MATUSETRANS", MXServer.getMXServer().getSystemUserInfo());
		matusetransSet.setWhere("itemnum = '" + itemNum+ "' and transdate > to_date('" + changedate+ "','yyyy-MM-dd HH24:mi:ss') order by transdate desc");
		matusetransSet.reset();
		if(!matusetransSet.isEmpty() && matusetransSet.count() > 0){
		Double quantity = matusetransSet.sum("quantity");		
		if ( quantity != 0){
			Object params[] = { "Note: The item "+ itemNum+ " has some issue records or not all return , not allow to make credit note!" };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		   }
	    }
		matusetransSet.close();
     }
		invoicestatusSet.close();
	}
	
	private void nopricelimit(String itemNum, String location) throws RemoteException, MXException{
		MboSetRemote invbalancesSet = MXServer.getMXServer().getMboSet("INVBALANCES", MXServer.getMXServer().getSystemUserInfo());
		invbalancesSet.setWhere(" itemnum='"+itemNum+"' and location='"+location+"' ");
		invbalancesSet.reset();
		if (!invbalancesSet.isEmpty() && invbalancesSet.count() > 0) {
			double curbal = invbalancesSet.sum("curbal");

			if (curbal == 0) {
				Object params[] = { "Note: The current average unit price is 0. \nNo need to adjust the average unit price." };
				throw new MXApplicationException("instantmessaging", "tsdimexception",params);
			}
		}
		invbalancesSet.close();
	}
	
    private void handleCredit(MboRemote invoiceLine, MboRemote poMbo, double credit, MboSetRemote matRecTransSet) throws MXException, RemoteException {

        if (String.valueOf(credit) != null  && !String.valueOf(credit).equalsIgnoreCase("")) {
        	createMatRecTransactions(invoiceLine, matRecTransSet, credit);
        }

        showMessageBox("Success: Make credit note  ! The value is " + invoiceLine.getString("udcreditvalue")+" .");
        matRecTransSet.save();
    }
	
    private void createMatRecTransactions(MboRemote invoiceLine, MboSetRemote matRecTransSet, double credit) throws MXException, RemoteException {
        MboRemote returnTransaction = matRecTransSet.add(11L);
        setMatRecTransactionValues(returnTransaction, invoiceLine, credit, "RETURN", -0.01, false);
        matRecTransSet.save();
    	
    	MboRemote receiptTransaction = matRecTransSet.add(11L);
        setMatRecTransactionValues(receiptTransaction, invoiceLine, credit, "RECEIPT", 0.01, true);
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
            matRec.setValue("currencylinecost", credit, 11L);
            matRec.setValue("unitcost", credit * 100, 2L);
            matRec.setValue("linecost", matRec.getDouble("unitcost") * matRec.getDouble("quantity"), 2L);
            matRec.setValue("loadedcost", matRec.getDouble("unitcost") * matRec.getDouble("quantity"), 11L);
            matRec.setValue("actualcost", matRec.getDouble("unitcost"), 11L);
            matRec.setValue("currencyunitcost", matRec.getDouble("unitcost"), 11L);
            matRec.setValue("tax1", getTaxRate(invoiceLine, matRec), 11L);
        } else {
        	invoiceLine.setValue("udcreditvalue",  credit, 11L);
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
