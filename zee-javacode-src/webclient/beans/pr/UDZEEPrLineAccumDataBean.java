package guide.webclient.beans.pr;

import guide.app.common.CommonUtil;
import guide.app.po.UDPO;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.app.po.PO;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.webclient.system.runtime.WebClientRuntime;

import java.util.Date;

import psdi.app.pr.PR;

/**
 *@function:Purchase accumulation view
 *@author:zj
 *@date:2024-09-27 11:13:10
 *@modify:
 */
public class UDZEEPrLineAccumDataBean extends DataBean {
	
	@Override
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote mbo = this.app.getAppBean().getMbo(); //PR
		MboRemote thismbo = this.getMbo(); //虚拟表UDACCUMPO
		MboRemote owner = thismbo.getOwner(); //PRLINE
		if (owner!=null) {
			Vector<MboRemote> vec = owner.getThisMboSet().getSelection();
			if (vec.size() == 0) {
				Object params[] = { "Select at least one prline." };
				throw new MXApplicationException("instantmessaging", "tsdimexception",params);
			}
			setValue("vendor", owner.getString("udprevendor"), 11L);
		}
	}
	
	//DJY: 根据不同的prlines，新建PO
	public void UDZEECREATEPO() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo(); //PR
		MboRemote thismbo = this.getMbo(); //虚拟表UDACCUMPO
		MboRemote owner = thismbo.getOwner(); //PRLINE
		String vendor = thismbo.getString("vendor");

		if (owner!=null) {
			Vector<MboRemote> vec = owner.getThisMboSet().getSelection();
			if (vec.size() > 0) {
				MboRemote pr = null;
				MboRemote createPOHeader = null;
				
				MboRemote selectMbo0 = (MboRemote) vec.elementAt(0);//始终取第一条prline的pr主信息给新PO
				pr = selectMbo0.getMboSet("PR").getMbo(0);	
				
				MboSetRemote vendornameSet = thismbo.getMboSet("COMPANIES");
				String vendorname = vendornameSet.getMbo(0).getString("name");
								
				createPOHeader = addPRToCreatePO(pr,"New PO from:"+vendorname);
				createPOHeader.setValue("vendor", vendor,2L);
				
				MboSetRemote newPOLineSet = createPOHeader.getMboSet("POLINE");
			
				for (int i = 0; i < vec.size(); i++) {
					MboRemote selectMbo = (MboRemote) vec.elementAt(i);//prline
					pr = selectMbo.getMboSet("PR").getMbo(0);
					((UDPO) createPOHeader).createPOLineFromPR(pr, selectMbo, newPOLineSet);
				}
				
				pr.getThisMboSet().save();
				
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Tips: "+vec.size()+" PRLines has created new PO "+createPOHeader.getString("ponum"), 1);
				WebClientRuntime.sendEvent(new WebClientEvent("dialogclose", "UDACCUMPRLINE", null, clientSession));
				WebClientRuntime.sendEvent(new WebClientEvent("dialogclose", "UDACCUM", null, clientSession));
				WebClientRuntime.sendEvent(new WebClientEvent("dialogclose", "UDACCUMPO", null, clientSession));
			}
		}
	}
		
	private MboRemote addPRToCreatePO(MboRemote fromPR,String description) throws MXException,RemoteException {
		UDPO createPOHeader = (UDPO) ((PR) fromPR).createPOHeaderFromPR("", description, ((PR) fromPR));
		String ponum = createPOHeader.getString("ponum");
		String personid = fromPR.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		String udapptype = fromPR.getString("udapptype");
		Date requireddate = fromPR.getDate("requireddate");
		String udmatstatus = fromPR.getString("udmatstatus");
		String currency = fromPR.getString("udcurrency");
		String udcompany = fromPR.getString("udcompany");
		double ukurs = 1;
		if (!fromPR.isNull("udukurs")) {
			ukurs = fromPR.getDouble("udukurs");
		}
		String apptype = udapptype.replaceAll("PR", "PO");
		createPOHeader.setValue("udapptype", apptype, 11L);
		createPOHeader.setValue("udpurplat", "DPO", 11L);
		createPOHeader.setValue("udcreateby", personid, 2L);
		createPOHeader.setValue("udcreatetime", currentDate, 11L);
		createPOHeader.setValue("requireddate", requireddate, 11L);
		createPOHeader.setValue("udmatstatus", udmatstatus, 11L);
		createPOHeader.setValue("udcurrency", currency, 11L);
		createPOHeader.setValue("udukurs", ukurs, 11L);
		createPOHeader.setValue("udrevponum", ponum, 11L);
		createPOHeader.setValue("udrevnum", 0, 11L);
		createPOHeader.setValue("udcompany", udcompany, 11L);
		
		MboSetRemote prLineSet = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
		prLineSet.setWhere("prnum = '"+fromPR.getString("prnum")+"' ");
		prLineSet.reset();
		if (!prLineSet.isEmpty() && prLineSet.count() > 0) {
		MboRemote oldprlineRemote = prLineSet.getMbo(0);
		String purchaseagent = oldprlineRemote.getString("udpurchaser");
		if(!purchaseagent.equalsIgnoreCase("")){
			createPOHeader.setValue("purchaseagent", purchaseagent, 2L);  //采购员需要换成PRLINE.UDPURCHASER,如果PRLINE.UDPURCHASER非空，则取第一条的采购员
		}else{
			createPOHeader.setValue("purchaseagent", personid, 2L);//如果PRLINE.UDPURCHASER为空，则取PR当前创建人
		}
	}
		prLineSet.close();
		
		return createPOHeader;
	}
	
	public int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo(); //PR
		MboRemote thismbo = this.getMbo(); //虚拟表UDACCUMPO
		if (thismbo.getString("ponum") == null || thismbo.getString("ponum").equalsIgnoreCase("")) {
			Object params[] = { "Please select one PO." };
			throw new MXApplicationException("instantmessaging", "tsdimexception",params);
		}
		MboRemote owner = thismbo.getOwner(); //PRLINE
		if (owner!=null) {
			Vector<MboRemote> vec = owner.getThisMboSet().getSelection();
			if (vec.size() > 0) {
				String ponum = getString("ponum");
				MboRemote po = null;
				MboRemote pr = null;
				MboSetRemote polineSet = null;
				MboSetRemote poSet = MXServer.getMXServer().getMboSet("PO", MXServer.getMXServer().getSystemUserInfo());
				poSet.setWhere(" ponum='"+ponum+"' ");
				poSet.reset();
				if (!poSet.isEmpty() && poSet.count() > 0) {
					po = poSet.getMbo(0);
					polineSet = po.getMboSet("POLINE");
				}
				for (int i = 0; i < vec.size(); i++) {
					MboRemote selectMbo = (MboRemote) vec.elementAt(i);
					pr = selectMbo.getMboSet("PR").getMbo(0);
					addPRLineToPOLine(pr, selectMbo, polineSet, po);
					
				}
				pr.getThisMboSet().save();
				polineSet.save();
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "", "Tips: "+vec.size()+" PRLines has been added to "+getString("ponum"), 1);
				WebClientRuntime.sendEvent(new WebClientEvent("dialogclose", "UDACCUMPRLINE", null, clientSession));
				WebClientRuntime.sendEvent(new WebClientEvent("dialogclose", "UDACCUM", null, clientSession));
			}
		}
		return 1;
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
