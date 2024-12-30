package guide.app.security;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPermitJobLevel extends MAXTableDomain {

	public FldPermitJobLevel(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ALNDOMAIN", "value=:" + thisAttr);
		String[] FromStr = { "value" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String appName = mbo.getThisMboSet().getApp();
		if ("udpermitf".equalsIgnoreCase(appName)) {
			setListCriteria("domainid = 'UDJOBLEVELF'");
		} else if ("udpermith".equalsIgnoreCase(appName)) {
			setListCriteria("domainid = 'UDJOBLEVELH'");
		}
		return super.getList();
	}
}
