package guide.app.rfq;

import java.rmi.RemoteException;

import guide.app.project.ProCon;
import psdi.app.rfq.FldRFQVendorVendor;
import psdi.app.rfq.RFQVendor;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldRFQVendorVendor extends FldRFQVendorVendor {

	public UDFldRFQVendorVendor(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDRFQ) {
			String udcompany = owner.getString("udcompany");
			listSet.setWhere(
					"exists (select 1 from udcomptaxcode where company=companies.company and disabled=0 and udcompany='"
							+ udcompany + "')");
		}
		return listSet;
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		RFQVendor mbo = (RFQVendor) this.getMboValue().getMbo();
		if (!this.getMboValue().isNull()) {
			MboSetRemote companiesSet = mbo.getMboSet("UDCOMPTAXCODE");
			if (!companiesSet.isEmpty() && companiesSet.count() > 0) {
				MboRemote companies = companiesSet.getMbo(0);
				String tax1code = companies.getString("tax1code");
				MboSetRemote rfqLineSet = mbo.getMboSet("UDRFQLINE");
				if (!rfqLineSet.isEmpty() && rfqLineSet.count() > 0) {
					for (int i = 0; rfqLineSet.getMbo(i) != null; i++) {
						UDRFQLine rfqLine = (UDRFQLine) rfqLineSet.getMbo(i);
						rfqLine.CustCopyRFQLinesToQuotationLines(this.getMboValue().getString());
					}
				}
				mbo.setValue("udtax1code", tax1code, 2L);
			}
		}
	}
}
