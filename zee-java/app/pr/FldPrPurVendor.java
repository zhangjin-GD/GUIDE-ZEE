package guide.app.pr;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurVendor;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPrPurVendor extends FldPurVendor {

	public FldPrPurVendor(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		Mbo mbo = this.getMboValue().getMbo();
		String udcompany = mbo.getString("udcompany");
		listSet.setWhere(
				"exists (select 1 from udcomptaxcode where company=companies.company and disabled=0 and udcompany='"
						+ udcompany + "')");
		return listSet;
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		// pct不更新税率
		String udcompany = mbo.getString("udcompany");
		if (!"GR02PCT".equalsIgnoreCase(udcompany)) {
			MboSetRemote compTaxSet = mbo.getMboSet("UDCOMPTAXCODE");
			if (!compTaxSet.isEmpty() && compTaxSet.count() > 0) {
				MboRemote compTax = compTaxSet.getMbo(0);
				String tax1code = compTax.getString("TAX1CODE");
				MboSetRemote lineSet = mbo.getMboSet("PRLINE");
				if (!lineSet.isEmpty() && lineSet.count() > 0) {
					for (int i = 0; lineSet.getMbo(i) != null; i++) {
						MboRemote line = lineSet.getMbo(i);
						line.setValue("tax1code", tax1code, 2L);
					}
				}
			}
		}
	}
}
