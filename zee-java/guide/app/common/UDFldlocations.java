package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldlocations extends MAXTableDomain {

	public UDFldlocations(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("LOCATIONS", "LOCATION=:" + thisAttr);
		String[] FromStr = { "LOCATION" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String company = mbo.getString("udcompany");
		setListCriteria("udcompany='" + company + "'");
		return super.getList();
	}

}
