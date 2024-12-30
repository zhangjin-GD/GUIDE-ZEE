package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDAssetTypeCode extends MAXTableDomain{
	public UDAssetTypeCode(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDASSETTYPE", "CODE=:" + thisAttr);
		String[] FromStr = { "CODE" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
			setListCriteria("1 = 1");
		return super.getList();
	}	
}
