package guide.app.rfq;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.contract.Contract;
import guide.app.contract.ContractLine;
import guide.app.contract.ContractLineSet;
import guide.app.contract.ContractSet;
import guide.app.po.UDPO;
import psdi.app.rfq.RFQVendor;
import psdi.app.rfq.RFQVendorRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDRFQVendor extends RFQVendor implements RFQVendorRemote {

	public UDRFQVendor(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public MboRemote createPOHeaderFromRFQ(String ponum, String description) throws MXException, RemoteException {

		UDPO newPo = (UDPO) super.createPOHeaderFromRFQ(ponum, description);
		UDRFQ rfq = (UDRFQ) this.getOwner();
		if (rfq != null) {
			double ukurs = 1;
			String personid = this.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			String udapptype = rfq.getString("udapptype");
			String udcgly = rfq.getString("udcgly");
			String udcglb = rfq.getString("udcglb");
			String apptype = udapptype.replaceAll("RFQ", "PO");
			newPo.setValue("udapptype", apptype, 11L);
			String udcompany = rfq.getString("udcompany");
			if(!udcompany.equalsIgnoreCase("") && udcompany.equalsIgnoreCase("ZEE")){
				newPo.setValue("udapptype", "POZEE", 11L);
			}
			newPo.setValue("udcreateby", personid, 2L);// 创建人
			newPo.setValue("udcreatetime", currentDate, 11L);// 创建时间
			newPo.setValueNull("purchaseagent", 2L);
			newPo.setValue("purchaseagent", personid, 2L);
			newPo.setValue("udrevponum", ponum, 11L);
			newPo.setValue("udrevnum", 0, 11L);
			newPo.setValue("udcgly", udcgly, 11L);
			newPo.setValue("udcglb", udcglb, 11L);
			MboSetRemote companySet = newPo.getMboSet("UDCOMPANY");
			if (!companySet.isEmpty() && companySet.count() > 0) {
				String currency = companySet.getMbo(0).getString("currency");
				newPo.setValue("udcurrency", currency, 11L);
				MboSetRemote currexchSet = newPo.getMboSet("UDCURREXCH");
				if (!currexchSet.isEmpty() && currexchSet.count() > 0) {
					ukurs = currexchSet.getMbo(0).getDouble("ukurs");
				}
			}
			newPo.setValue("udukurs", ukurs, 11L);
		}
		return newPo;
	}

	public void createConFromRFQ(MboRemote mbo) throws RemoteException, MXException {
		String description = mbo.getString("description");
		String contractno = mbo.getString("contractno");
		Date startdate = mbo.getDate("startdate");
		Date enddate = mbo.getDate("enddate");
		double limitmaxcost = mbo.getDouble("limitmaxcost");
		String vendor = this.getString("vendor");
		String rfqnum = this.getString("rfqnum");
		ContractSet contSet = (ContractSet) this.getMboSet("$UDCONTRACT", "UDCONTRACT", "1=2");
		Contract cont = (Contract) contSet.add();
		cont.setValue("description", description, 11L);
		cont.setValue("contractno", contractno, 11L);
		cont.setValue("startdate", startdate, 11L);
		cont.setValue("enddate", enddate, 11L);
		cont.setValue("limitmaxcost", limitmaxcost, 11L);
		cont.setValue("vendor", vendor, 11L);
		cont.setValue("rfqnum", rfqnum, 11L);

		ContractLineSet contLineSet = (ContractLineSet) cont.getMboSet("UDCONTRACTLINE");
		MboSetRemote quotationLineSet = this.getMboSet("UDISAWARDED");
		if (!quotationLineSet.isEmpty() && quotationLineSet.count() > 0) {
			for (int i = 0; quotationLineSet.getMbo(i) != null; i++) {
				ContractLine contLine = (ContractLine) contLineSet.add();
				MboRemote quotationLine = quotationLineSet.getMbo(i);
				int rfqlineid = quotationLine.getInt("rfqline.rfqlineid");
				int prlineid = quotationLine.getInt("rfqline.prline.prlineid");
				String linetype = quotationLine.getString("rfqline.linetype");
				String itemnum = quotationLine.getString("itemnum");
				String lineDesc = quotationLine.getString("description");
				String remark = quotationLine.getString("rfqline.remark");
				double orderqty = quotationLine.getDouble("orderqty");
				String orderunit = quotationLine.getString("orderunit");
				String tax1code = quotationLine.getString("tax1code");
				double udtotalprice = quotationLine.getDouble("udtotalprice");// 含税单价
				double udtotalcost = quotationLine.getDouble("udtotalcost");// 含税总价
				double unitcost = quotationLine.getDouble("unitcost");// 不含税单价
				double linecost = quotationLine.getDouble("linecost");// 不含税总价
				double tax1 = quotationLine.getDouble("tax1");// 税额

				contLine.setValue("linetype", linetype, 11L);
				contLine.setValue("itemnum", itemnum, 11L);
				contLine.setValue("description", lineDesc, 11L);
				contLine.setValue("remarks", remark, 11L);
				contLine.setValue("orderqty", orderqty, 11L);
				contLine.setValue("orderunit", orderunit, 11L);
				contLine.setValue("tax1code", tax1code, 11L);
				contLine.setValue("totalunitcost", udtotalprice, 11L);
				contLine.setValue("totallinecost", udtotalcost, 11L);
				contLine.setValue("unitcost", unitcost, 11L);
				contLine.setValue("linecost", linecost, 11L);
				contLine.setValue("tax1", tax1, 11L);
				contLine.setValue("rfqlineid", rfqlineid, 11L);
				contLine.setValue("prlineid", prlineid, 11L);
			}
			UDRFQ rfq = (UDRFQ) this.getOwner();
			if (rfq.getString("udcompany").equalsIgnoreCase("AE03ADT")) {
				rfq.setValue("status", "CLOSE", 11L);
				rfq.setValue("historyflag", "1", 11L);
			}
		}
	}
}
