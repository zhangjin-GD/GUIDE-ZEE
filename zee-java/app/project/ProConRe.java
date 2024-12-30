package guide.app.project;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class ProConRe extends UDMbo implements MboRemote {

	private final int KEYLEN = 2; // 编号流水号长度

	public ProConRe(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		this.setValue("totalcost", 0, 11L);
		this.setValue("tax", 0, 11L);
		this.setValue("pretaxcost", 0, 11L);
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if (!getMboValue("status").getInitialValue().asString().equalsIgnoreCase("APPR")
				&& getString("status").equalsIgnoreCase("APPR")) {

			ProConSet proConSet = (ProConSet) this.getMboSet("UDPROCON");
			if (!proConSet.isEmpty()) {

				ProCon proCon = (ProCon) proConSet.getMbo(0);
				String proconnum = proCon.getString("proconnum");
				String description = proCon.getString("description");
				String udcompany = proCon.getString("udcompany");
				String uddept = proCon.getString("uddept");
				String udofs = proCon.getString("udofs");
				String probidnum = proCon.getString("probidnum");
				String status = proCon.getString("status");
				Date statustime = proCon.getDate("statustime");
				String createby = proCon.getString("createby");
				Date createtime = proCon.getDate("createtime");
				String apprby = proCon.getString("apprby");
				Date apprtime = proCon.getDate("apprtime");

				String projectnum = this.getString("projectnum");
				String budgetnum = this.getString("budgetnum");
				String vendor = this.getString("vendor");
				String vendorname = this.getString("vendorname");
				String currencycode = this.getString("currencycode");
				double totalcost = this.getDouble("totalcost");
				double tax = this.getDouble("tax");
				double pretaxcost = this.getDouble("pretaxcost");
				Date deliverydate = this.getDate("deliverydate");
				String contactby = this.getString("contactby");
				String phone = this.getString("phone");
				String bankname = this.getString("bankname");
				String bankaccountnum = this.getString("bankaccountnum");

				String keyNum = CommonUtil.autoKeyNum("UDPROCON", "PROCONNUM", proconnum + '-', "", KEYLEN);

				MboRemote proConNew = proConSet.add();
				proConNew.setValue("proconnum", proconnum, 11L);
				proConNew.setValue("description", description, 11L);
				proConNew.setValue("udcompany", udcompany, 11L);
				proConNew.setValue("uddept", uddept, 11L);
				proConNew.setValue("udofs", udofs, 11L);
				proConNew.setValue("projectnum", projectnum, 11L);
				proConNew.setValue("budgetnum", budgetnum, 11L);
				proConNew.setValue("probidnum", probidnum, 11L);
				proConNew.setValue("status", status, 11L);
				proConNew.setValue("statustime", statustime, 11L);
				proConNew.setValue("createby", createby, 11L);
				proConNew.setValue("createtime", createtime, 11L);
				proConNew.setValue("apprby", apprby, 11L);
				proConNew.setValue("apprtime", apprtime, 11L);

				proConNew.setValue("vendor", vendor, 11L);
				proConNew.setValue("vendorname", vendorname, 11L);
				proConNew.setValue("currencycode", currencycode, 11L);
				proConNew.setValue("totalcost", totalcost, 11L);
				proConNew.setValue("tax", tax, 11L);
				proConNew.setValue("pretaxcost", pretaxcost, 11L);
				proConNew.setValue("deliverydate", deliverydate, 11L);
				proConNew.setValue("contactby", contactby, 11L);
				proConNew.setValue("phone", phone, 11L);
				proConNew.setValue("bankname", bankname, 11L);
				proConNew.setValue("bankaccountnum", bankaccountnum, 11L);

				MboSetRemote proConLineNewSet = proConNew.getMboSet("UDPROCONLINE");
				MboSetRemote mboLineSet = this.getMboSet("UDPROCONRELINE");
				if (!mboLineSet.isEmpty()) {

					for (int i = 0; mboLineSet.getMbo(i) != null; i++) {
						MboRemote mboLine = mboLineSet.getMbo(i);
						String lineproconnum = mboLine.getString("proconnum");
						int proconlinenum = mboLine.getInt("proconlinenum");
						String paytype = mboLine.getString("paytype");
						String payterm = mboLine.getString("payterm");
						Date paiddate = mboLine.getDate("paiddate");
						double paycost = mboLine.getDouble("paycost");
						String paystatus = mboLine.getString("paystatus");

						MboRemote proConLineNew = proConLineNewSet.add();

						proConLineNew.setValue("proconnum", lineproconnum, 11L);
						proConLineNew.setValue("proconlinenum", proconlinenum, 11L);
						proConLineNew.setValue("paytype", paytype, 11L);
						proConLineNew.setValue("payterm", payterm, 11L);
						proConLineNew.setValue("paiddate", paiddate, 11L);
						proConLineNew.setValue("paycost", paycost, 11L);
						proConLineNew.setValue("paystatus", paystatus, 11L);
					}
				}
				// 原合同行设置新编号
				MboSetRemote proConLineSet = proCon.getMboSet("UDPROCONLINE");
				for (int i = 0; proConLineSet.getMbo(i) != null; i++) {

					MboRemote proConLine = proConLineSet.getMbo(i);
					proConLine.setValue("proconnum", keyNum, 11L);

				}
				// 原合同设置新编号
				proCon.setValue("proconnum", keyNum, 11L);
				proCon.setValue("status", "REVISD", 11L);
			}
		}
	}
}
