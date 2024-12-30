package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldUDLocation extends MAXTableDomain {

	public UDFldUDLocation(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("LOCATIONS", "LOCATION=:" + thisAttr);
		String[] FromStr = { "LOCATION" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote udlocation = mbo.getMboSet("UDLOCATION");
		MboSetRemote udlocations = mbo.getMboSet("UDLOCATIONS");
		if(!udlocation.isEmpty()&&udlocation.count()>0){
			udlocation.deleteAll();
		}
		if(!udlocations.isEmpty()&&udlocations.count()>0){
			udlocations.deleteAll();
		}
	}
	
	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String company = mbo.getString("udcompany");
		setListCriteria("udcompany='"+company+"'");
		return super.getList();
	}

}
