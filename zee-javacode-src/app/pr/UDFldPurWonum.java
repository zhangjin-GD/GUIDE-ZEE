package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldPurWonum extends MAXTableDomain {

	public UDFldPurWonum(MboValue mbv) throws MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("WORKORDER", "wonum =:" + thisAttr);
		String[] FromStr = { "wonum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDPR) {
			String udcompany = owner.getString("udcompany");
			setListCriteria("udcompany='" + udcompany + "' and status not in ('CAN','CLOSE')");
		}
		return super.getList();
	}
}
