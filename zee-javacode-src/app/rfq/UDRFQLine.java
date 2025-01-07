package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.common.TaxUtility;
import psdi.app.rfq.RFQLine;
import psdi.app.rfq.RFQLineRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDRFQLine extends RFQLine implements RFQLineRemote {

	public UDRFQLine(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote owner = this.getOwner();
		if (owner != null) {
			String udapptype = owner.getString("udapptype");
			if ("RFQMAT".equalsIgnoreCase(udapptype)) {
				this.setValue("linetype", "ITEM", 2L);
			}
			if ("RFQFIX".equalsIgnoreCase(udapptype)) {
				this.setValue("linetype", "ITEM", 2L);
			}
			if ("RFQSER".equalsIgnoreCase(udapptype)) {
				this.setValue("linetype", "SERVICE", 2L);
			}
		}
	}

	public void CustCopyRFQLinesToQuotationLines(String vendor) throws MXException, RemoteException {
		if (!this.isNull("storeloc")) {
			String[] params;
			if (this.isNull("quotationorderunit")) {
				params = new String[] { this.getMboValue("quotationorderunit").getColumnTitle(),
						this.getString("RFQLINENUM") };
				throw new MXApplicationException("rfq", "enterQuotationInfoRequiredForStoreLoc", params);
			}

			if (this.isNull("conversion")) {
				params = new String[] { this.getMboValue("conversion").getColumnTitle(), this.getString("RFQLINENUM") };
				throw new MXApplicationException("rfq", "enterQuotationInfoRequiredForStoreLoc", params);
			}
		}

		MboSetRemote quotationLineSet = this.getMboSet("QUOTATIONLINE");
		quotationLineSet.setFlag(7L, false);
		MboRemote quotationLine = quotationLineSet.add(2L);
		if (!this.isNull("storeloc") && !this.isNull("itemnum")) {
			SqlFormat sqf = new SqlFormat(this,
					"itemnum = :1 and itemsetid = :2 and location = :3 and siteid = :4 and vendor=:5");
			sqf.setObject(1, "inventory", "itemnum", this.getString("itemnum"));
			sqf.setObject(2, "inventory", "itemsetid", this.getString("itemsetid"));
			sqf.setObject(3, "inventory", "location", this.getString("storeLoc"));
			sqf.setObject(4, "inventory", "siteid", this.getString("siteid"));
			sqf.setObject(5, "inventory", "vendor", vendor);
			MboSetRemote inventorySet = this.getMboSet("$inventory", "INVENTORY", sqf.format());
			if (inventorySet.getMbo(0) != null) {
				quotationLine.setValue("catalogcode", inventorySet.getMbo(0).getString("catalogcode"), 11L);
			}
		}

		quotationLine.setValue("rfqnum", this.getString("rfqnum"), 2L);
		quotationLine.setValue("rfqlinenum", this.getString("rfqlinenum"), 2L);
		quotationLine.setValue("vendor", vendor, 11L);
		TaxUtility taxUtility = TaxUtility.getTaxUtility();
		taxUtility.zeroAllTaxes(quotationLine, "TAX", 11L);
		quotationLine.setValue("itemnum", this.getString("itemnum"), 2L);
		quotationLine.setValue("itemsetid", this.getString("itemsetid"), 2L);
		quotationLine.setValue("description", this.getString("description"), 11L);
		quotationLine.setValue("description_longdescription", this.getString("description_longdescription"), 11L);
		quotationLine.setValue("linetype", this.getString("linetype"), 11L);
		quotationLine.setValue("manufacturer", this.getString("manufacturer"), 11L);
		quotationLine.setValue("modelnum", this.getString("modelnum"), 11L);
		quotationLine.setValue("orderqty", this.getString("orderqty"), 11L);
		//ZEE-将PRLINE的issueqty赋值给quotationline 2025-01-07 17:16:48
		quotationLine.setValue("udissueqty", this.getString("PRLINE.udissueqty"), 11L);
		
		quotationLine.setValue("orderunit", this.getString("quotationorderunit"), 11L);
		quotationLine.setValue("commodity", this.getString("commodity"), 11L);
		quotationLine.setValue("commoditygroup", this.getString("commoditygroup"), 11L);
		quotationLine.setValue("conditioncode", this.getString("conditioncode"), 11L);
		quotationLine.setValue("memo", this.getString("remark"), 11L);
		quotationLine.setValue("memo_longdescription", this.getString("remark_longdescription"), 11L);
		if (quotationLine.getString("tax1code") == "") {
			SqlFormat sqlf = new SqlFormat(this, "company = :1 and orgid =:2");
			sqlf.setObject(1, "COMPANIES", "company", vendor);
			sqlf.setObject(2, "COMPANIES", "orgid", this.getString("orgid"));
			MboSetRemote companySetRemote = this.getMboSet("$companies", "companies", sqlf.format());
			MboRemote companyRemote = companySetRemote.getMbo(0);
			if (companyRemote != null) {
				quotationLine.setValue("glcreditacct", companyRemote.getString("rbniacc"), 11L);
				taxUtility.setTaxattrValue(quotationLine, "TAXCODE", companyRemote, 2L);
			}
		}
		MboRemote rfqVendor = this.getOwner();
		if (rfqVendor != null) {
			MboRemote rfq = rfqVendor.getOwner();
			if (rfq != null) {
				String udcompany = rfq.getString("udcompany");
				MboSetRemote udcompTaxSet = this.getMboSet("$udcomptaxcode", "udcomptaxcode",
						"company='" + vendor + "' and udcompany='" + udcompany + "'");
				if (!udcompTaxSet.isEmpty() && udcompTaxSet.count() > 0) {
					MboRemote udcompTax = udcompTaxSet.getMbo(0);
					String tax1code = udcompTax.getString("tax1code");
					quotationLine.setValue("tax1code", tax1code, 2L);
				}
			}
		}

	}
}
