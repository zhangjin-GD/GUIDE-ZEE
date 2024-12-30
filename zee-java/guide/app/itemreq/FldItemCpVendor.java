package guide.app.itemreq;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldItemCpVendor extends MAXTableDomain {

	public FldItemCpVendor(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("COMPANIES", "COMPANY=:" + thisAttr);
		String[] FromStr = { "COMPANY" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
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
}
