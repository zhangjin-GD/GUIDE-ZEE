package guide.app.company;

import java.rmi.RemoteException;
import java.util.Map;

import psdi.app.company.Company;
import psdi.app.company.CompanyRemote;
import psdi.mbo.LinkedMboRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDCompany extends Company implements CompanyRemote, LinkedMboRemote {

	public UDCompany(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		this.getMboValue("company").autoKey();
		String company = getString("company");
		this.setValue("company", company, 11L);
		this.setValue("udsource", "EAM", 11L);
		this.setValue("tax1code", "13", 11L);
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if (this.toBeAdded()) {
			MboSetRemote deptSet = this.getMboSet("UDDEPTTAX");
			MboSetRemote compTaxSet = this.getMboSet("UDCOMPTAXCODE");
			String company = this.getString("company");
			if (!deptSet.isEmpty() && deptSet.count() > 0) {
				for (int i = 0; deptSet.getMbo(i) != null; i++) {
					MboRemote dept = deptSet.getMbo(i);
					String deptnum = dept.getString("deptnum");
					String tax1code = dept.getString("tax1code");
					MboRemote compTax = compTaxSet.add();
					compTax.setValue("company", company, 11L);
					compTax.setValue("udcompany", deptnum, 11L);
					compTax.setValue("tax1code", tax1code, 11L);
				}
			}
		}
	}

	public void setCompToMdm(Map<String, Object> map, String type) throws Exception {
		String company = map.get("vENDOR_CODE").toString();// 供应商编号
		String name = map.get("vENDOR_NAME").toString();// 供应商名称
//		String currency = map.get("rEGISTERED_CURRENCY").toString();// 货币
//		String homepage = map.get("oFFICIAL_WEBSITE").toString();// 主页
//		String bankname = map.get("bANK_CODE").toString();// 开户银行名称
//		String bankaccountnum = map.get("bANK_ACCOUNT").toString();// 银行账号
//		String address1 = map.get("rEGISTERED_ADDRESS").toString();// 注册地址
//		String address4 = map.get("pOST_CODE").toString();// 邮编
//		String registration1 = map.get("sOCIAL_CREDIT_CODE").toString();// 社会信用代码
//		String udbusiness = map.get("bUSINESS_SCOPE").toString();// 业务范围
//		String phone = map.get("tELEPHONE").toString();// 电话
//		String address2 = map.get("cOUNTRY").toString();// 国家

		if ("ADD".equalsIgnoreCase(type)) {
			this.setValue("inclusive1", true, 11L);
		}

//		if (currency.contains(",")) {
//			currency = currency.substring(0, currency.indexOf(","));
//		}

//		MboSetRemote currencySet = this.getMboSet("$CURRENCY", "CURRENCY", "currencycode ='" + currency + "'");
//		if (currencySet != null && !currencySet.isEmpty()) {
//			MboRemote currencyMbo = currencySet.getMbo(0);
//			this.setValue("currencycode", currencyMbo.getString("currencycode"), 2L);
//		}
//		if (!bankaccountnum.isEmpty()) {
//			MboSetRemote bankinfoSet = this.getMboSet("$UDBANKINFO", "UDBANKINFO",
//					"bankaccountnum ='" + bankaccountnum + "'");
//			if (bankinfoSet != null && !bankinfoSet.isEmpty()) {
//				MboRemote bankinfo = bankinfoSet.getMbo(0);
//				bankinfo.setValue("bankname", bankname, 11L);
//				bankinfo.setValue("bankaccountnum", bankaccountnum, 11L);
//			} else {
//				MboRemote bankinfo = bankinfoSet.add();
//				bankinfo.setValue("bankname", bankname, 11L);
//				bankinfo.setValue("bankaccountnum", bankaccountnum, 11L);
//				bankinfo.setValue("isdefault", true, 2L);
//			}
//		}
		this.setValue("udmdmnum", company, 11L);
		this.setValue("name", name, 11L);
//		this.setValue("homepage", homepage, 11L);
//		this.setValue("address1", address1, 11L);
//		this.setValue("address4", address4, 11L);
//		this.setValue("registration1", registration1, 11L);
//		this.setValue("udbusiness", udbusiness, 11L);
//		this.setValue("phone", phone, 11L);
//		this.setValue("address2", address2, 11L);
//		this.setValue("udcurrencycode", currency, 11L);
		this.setValue("udsource", "MDM", 11L);
		this.setValue("udsourcedate", MXServer.getMXServer().getDate(), 11L);
	}
}
