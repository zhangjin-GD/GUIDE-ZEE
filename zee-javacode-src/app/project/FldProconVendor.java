package guide.app.project;

import java.rmi.RemoteException;

import psdi.app.company.FldCompany;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldProconVendor extends FldCompany {

	public FldProconVendor(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote vendorSet = mbo.getMboSet("VENDOR");
		if (!vendorSet.isEmpty()) {
			MboRemote vendor = vendorSet.getMbo(0);

			String name = vendor.getString("name");// 供应商名称
			String currencycode = vendor.getString("currencycode");// 币种
			String contactby = vendor.getString("contact");// 联系人
			String phone = vendor.getString("phone");// 联系人手机号

			mbo.setValue("vendorname", name, 11L);
			mbo.setValue("currencycode", currencycode, 11L);
			mbo.setValue("contactby", contactby, 11L);
			mbo.setValue("phone", phone, 11L);

			MboSetRemote bankinfoSet = vendor.getMboSet("UDBANKINFOISDEFAULT");// 银行信息
			if (!bankinfoSet.isEmpty()) {
				MboRemote bankinfo = bankinfoSet.getMbo(0);
				String bankname = bankinfo.getString("bankname");
				String bankaccountnum = bankinfo.getString("bankaccountnum");
				mbo.setValue("bankname", bankname, 11L);
				mbo.setValue("bankaccountnum", bankaccountnum, 11L);
			}
		}
		if (this.getMboValue().isNull()) {
			mbo.setValueNull("vendorname", 11L);
			mbo.setValueNull("currencycode", 11L);
			mbo.setValueNull("contactby", 11L);
			mbo.setValueNull("bankname", 11L);
			mbo.setValueNull("phone", 11L);
			mbo.setValueNull("bankaccountnum", 11L);
		}
	}
}
