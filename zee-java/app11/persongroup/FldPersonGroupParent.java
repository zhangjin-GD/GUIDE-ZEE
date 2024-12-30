package guide.app.persongroup;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPersonGroupParent extends MAXTableDomain{

	public FldPersonGroupParent(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
        setRelationship("PERSONGROUP", "PERSONGROUP=:" + thisAttr);
        String[] FromStr = {"PERSONGROUP"};
        String[] ToStr = {thisAttr};
        setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
        String sql = "PERSONGROUP !=:PERSONGROUP";
        setListCriteria(sql);
		return super.getList();
	}
}
