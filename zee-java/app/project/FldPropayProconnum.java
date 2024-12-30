package guide.app.project;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPropayProconnum extends MAXTableDomain {
	
	public FldPropayProconnum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDPROCON", "PROCONNUM=:" + thisAttr);
		String[] FromStr = { "PROCONNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("status = 'APPR'");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote proConSet = mbo.getMboSet("UDPROCON");
		if (!proConSet.isEmpty()) {
			MboRemote proCon = proConSet.getMbo(0);
			String vendor = proCon.getString("vendor");
			String vendorname = proCon.getString("vendorname");
			String currencycode = proCon.getString("currencycode");
			Date deliverydate = proCon.getDate("deliverydate");

			String contactby = proCon.getString("contactby");
			String phone = proCon.getString("phone");
			String bankname = proCon.getString("bankname");
			String bankaccountnum = proCon.getString("bankaccountnum");

			mbo.setValue("vendor", vendor, 11L);
			mbo.setValue("vendorname", vendorname, 11L);
			mbo.setValue("currencycode", currencycode, 11L);
			mbo.setValue("deliverydate", deliverydate, 11L);
			mbo.setValue("contactby", contactby, 11L);
			mbo.setValue("phone", phone, 11L);
			mbo.setValue("bankname", bankname, 11L);
			mbo.setValue("bankaccountnum", bankaccountnum, 11L);
		}

		if (this.getMboValue().isNull()) {
			mbo.setValueNull("vendor", 11L);
			mbo.setValueNull("vendorname", 11L);
			mbo.setValueNull("currencycode", 11L);
			mbo.setValueNull("deliverydate", 11L);
			mbo.setValueNull("contactby", 11L);
			mbo.setValueNull("phone", 11L);
			mbo.setValueNull("bankname", 11L);
			mbo.setValueNull("bankaccountnum", 11L);
		}
	}
}
