package guide.app.signin;


import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


public class FldSchShiftCrewId extends MAXTableDomain
{

	public FldSchShiftCrewId(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDSCHCREW", "udschcrewid=:" + thisAttr);
		String[] FromStr = { "udschcrewid" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "schplannum=:schplannum";
		setListCriteria(sql);
		return super.getList();
	}
    
	public void validate() throws RemoteException, MXException {
		super.validate();
		
	}
	
    public void action() throws RemoteException, MXException{
    	super.action();
    	
    	MboRemote mbo = getMboValue().getMbo();
		MboSetRemote schCrewSet = mbo.getMboSet("UDSCHCREW");
		if (schCrewSet != null && !schCrewSet.isEmpty()) {
			MboRemote schCrew = schCrewSet.getMbo(0);
			mbo.setValue("description", schCrew.getString("description"), 11L);
		}

    }
    
    
}