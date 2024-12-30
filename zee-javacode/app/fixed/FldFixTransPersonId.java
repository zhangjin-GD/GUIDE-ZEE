package guide.app.fixed;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldFixTransPersonId extends MAXTableDomain {

	public FldFixTransPersonId(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("PERSON", "personid = :" + thisAttr);
		String[] FromStr = { "personid" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "udcompany=:udcompany";
		String thisAttr = getMboValue().getAttributeName();
		if(thisAttr != null && (thisAttr.equalsIgnoreCase("transferinadmin") || thisAttr.equalsIgnoreCase("transferinlead"))){
			sql += " and uddept=:transferindept";
		}else if(thisAttr != null && (thisAttr.equalsIgnoreCase("calloutadmin") || thisAttr.equalsIgnoreCase("calloutlead"))){
			sql += " and uddept=:calloutdept";
		}
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}
	
}
