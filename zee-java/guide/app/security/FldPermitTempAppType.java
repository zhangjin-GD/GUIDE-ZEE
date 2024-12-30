package guide.app.security;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPermitTempAppType extends MAXTableDomain {

	public FldPermitTempAppType(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("MAXAPPS", "app=:" + thisAttr);
		String[] FromStr = { "app" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("maintbname='UDPERMIT'");
		return super.getList();
	}
}
