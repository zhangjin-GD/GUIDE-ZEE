package guide.webclient.beans.po;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.app.po.PO;

/**
 *@function:ZEE-PO里选择有合同的PRLINE
 *@author:zj
 *@date:2024-09-19 09:54:18
 *@modify:
 */
public class UDZEESelConPrlineDataBean extends DataBean {
	
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote mbo = this.app.getAppBean().getMbo();
		String status = mbo.getString("status");
		if (status!=null && !status.equalsIgnoreCase("WAPPR")) {
			Object params[] = { "Only POs with a status of WAPPR can be operated!" };
			throw new MXApplicationException("instantmessaging", "tsdimexception",params);
		}
	}
	
	public MboSetRemote getMboSetRemote() throws MXException, RemoteException {
		String sql = "";
		MboSetRemote prLineSet = super.getMboSetRemote();
		String personid = clientSession.getUserInfo().getPersonId();
		sql = " polineid is null and prnum in (select prnum from pr where status='APPR' and udcompany='ZEE') and itemnum in (select itemnum from udcontractline where linetype = 'ITEM' and gconnum in (select gconnum from udcontract where status='APPR' and udcompany='ZEE' and to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd'))) ";
		prLineSet.setWhere(sql);
		prLineSet.reset();
		return prLineSet;
	}
	
	public int execute() throws MXException, RemoteException {
		Vector vec = getSelection();
		if (vec.size() == 0) {
			clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Please select at least one PRLine data !", 1);
			return 1;
		} else {
			MboRemote mbo = this.app.getAppBean().getMbo();
			MboSetRemote polineSet = mbo.getMboSet("POLINE");
			MboRemote selectMbo = null;
			for (int i = 0; i< vec.size(); i++) {
				selectMbo = (MboRemote) vec.elementAt(i); //勾选的数据
				MboRemote pr = selectMbo.getMboSet("PR").getMbo(0);
				if (selectMbo != null) {
					addPRLineToPOLine(pr, selectMbo, polineSet, mbo);
				}
				pr.getThisMboSet().save();
			}
			clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Added successfully !\nNumber of added : "+vec.size(), 1);
			this.app.getAppBean().save();
            this.app.getAppBean().reloadTable();
            this.app.getAppBean().refreshTable();
			return 1;
		}
	}
	
	
	private void addPRLineToPOLine(MboRemote fromPR, MboRemote fromPRLine, MboSetRemote poLines, MboRemote mbo) throws MXException,RemoteException {
		MboRemote toPOLine = ((PO) mbo).createPOLineFromPR(fromPR, fromPRLine, poLines);
		String tax1code = "";
		String materialType = CommonUtil.getValue(toPOLine, "ITEM", "udmaterialType");
		toPOLine.setValue("udmaterialtype", materialType, 11L);
		toPOLine.setValue("udprojectnum", fromPRLine.getString("udprojectnum"), 11L);
		toPOLine.setValue("udbudgetnum", fromPRLine.getString("udbudgetnum"), 11L);
		
		MboSetRemote comptaxcodeSet = mbo.getMboSet("UDCOMPTAXCODE");
		if (!fromPRLine.getString("tax1code").equalsIgnoreCase("")) {
			tax1code = fromPRLine.getString("tax1code");
		} else if (!comptaxcodeSet.isEmpty() && comptaxcodeSet.count() > 0) {
			MboRemote comptaxcode = comptaxcodeSet.getMbo(0);
			tax1code = comptaxcode.getString("tax1code");
		} else {
			tax1code = "1L";
		}
		
		toPOLine.setValue("tax1code", tax1code, 11L);
		toPOLine.setValue("udtotalprice", fromPRLine.getDouble("udtotalprice"), 11L);
		toPOLine.setValue("udtotalcost", fromPRLine.getDouble("udtotalcost"), 2L);
		
		toPOLine.setValue("udcostcenterzee", fromPRLine.getString("udcostcenterzee"),11L);
		toPOLine.setValue("udglzee", fromPRLine.getString("udglzee"),11L);
		
		
		MboSetRemote matConLineSet = MXServer.getMXServer().getMboSet("UDCONTRACTLINE",MXServer.getMXServer().getSystemUserInfo());
		matConLineSet.setWhere("linetype='ITEM' and itemnum='"+toPOLine.getString("itemnum")+"' and gconnum in (select gconnum from udcontract where status='APPR' and to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd') and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd') and udcompany='"+getString("udcompany")+"' and vendor='"+getString("vendor")+"')");
		matConLineSet.reset();
		if (!matConLineSet.isEmpty() && matConLineSet.count() > 0) {
			MboRemote matConLine = matConLineSet.getMbo(0);
			int contractlineid = matConLine.getInt("udcontractlineid");
			tax1code = matConLine.getString("tax1code");
			double taxrate = 0.0D;
			MboSetRemote taxSet = MXServer.getMXServer().getMboSet("TAX",MXServer.getMXServer().getSystemUserInfo());
			taxSet.setWhere(" taxcode='"+tax1code+"' ");
			taxSet.reset();
			if (!taxSet.isEmpty() && taxSet.count() >0) {
				MboRemote tax = taxSet.getMbo(0);
				taxrate = tax.getDouble("taxrate");
			}
			taxSet.close();
			double percentTaxRate = taxrate / 100;
			double uddiscountprice = matConLine.getDouble("uddiscountprice");// 含税单价
			toPOLine.setValue("udcontractlineid", contractlineid, 11L);
			toPOLine.setValue("tax1code", tax1code, 11L);
			toPOLine.setValue("unitcost", uddiscountprice, 11L);
			toPOLine.setValue("linecost", toPOLine.getDouble("unitcost") * toPOLine.getDouble("orderqty"), 11L);
			toPOLine.setValue("udtotalprice", uddiscountprice * (1 + percentTaxRate), 11L);
			toPOLine.setValue("udtotalcost", toPOLine.getDouble("udtotalprice") * toPOLine.getDouble("orderqty"), 11L);
			toPOLine.setValue("tax1", toPOLine.getDouble("udtotalcost") - toPOLine.getDouble("linecost"), 11L);
		}
		matConLineSet.close();
			
		toPOLine.setValue("udprojectnum", fromPRLine.getString("udprojectnum"), 11L);
		toPOLine.setValue("udcapex", fromPRLine.getString("udcapex"), 11L);
		toPOLine.setValue("udcosttype", fromPRLine.getString("udcosttype"), 11L);
		toPOLine.setValue("udcostcenterasset", fromPRLine.getString("udcostcenterasset"), 11L);
		if (toPOLine.getString("storeloc").equalsIgnoreCase("")) {
			toPOLine.setValue("storeloc", "ZEE-01", 11L);
		}
	}
	
}
