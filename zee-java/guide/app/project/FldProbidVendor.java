package guide.app.project;

import java.rmi.RemoteException;

import psdi.app.company.FldCompany;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldProbidVendor extends FldCompany {

	public FldProbidVendor(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote vendorSet = mbo.getMboSet("VENDOR");
		if (!vendorSet.isEmpty()) {
			MboRemote vendor = vendorSet.getMbo(0);
			String currencycode = vendor.getString("currencycode");
			String name = vendor.getString("name");
			mbo.setValue("vendorname", name, 11L);
			mbo.setValue("currencycode", currencycode, 11L);
		}
		if (this.getMboValue().isNull()) {
			mbo.setValueNull("vendorname", 11L);
			mbo.setValueNull("currencycode", 11L);
		}
	}
}
