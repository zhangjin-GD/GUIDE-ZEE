package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPOLineVendor extends MAXTableDomain {

	public FldPOLineVendor(MboValue mbv) throws MXException, RemoteException {
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
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			listSet.setWhere(
					"exists (select 1 from udcomptaxcode where company=companies.company and disabled=0 and udcompany='"
							+ udcompany + "')");
		}
		return listSet;
	}
}
