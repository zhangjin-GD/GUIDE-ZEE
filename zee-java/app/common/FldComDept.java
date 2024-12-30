package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldComDept extends MAXTableDomain {

	public FldComDept(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDDEPT", "deptnum = :" + thisAttr);
		String[] FromStr = { "deptnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		
		String thisAttr = getMboValue().getAttributeName();
		if ("udcompany".equalsIgnoreCase(thisAttr)) {
			setListCriteria("type = 'COMPANY'");
		} else if ("uddept".equalsIgnoreCase(thisAttr)) {
			setListCriteria("type = 'DEPARTMENT' and parent=:udcompany");
		} else if ("udofs".equalsIgnoreCase(thisAttr)) {
			setListCriteria("type = 'OFFICE' and parent=:uddept or parent in(select deptnum from uddept where parent=:uddept)");
		}
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		
		String thisAttr = getMboValue().getAttributeName();
		MboRemote mbo = this.getMboValue().getMbo();
		if ("udcompany".equalsIgnoreCase(thisAttr)) {
			mbo.setValueNull("uddept", 11L);
			mbo.setValueNull("udofs", 11L);
		} else if ("uddept".equalsIgnoreCase(thisAttr)) {
			mbo.setValueNull("udofs", 11L);
		} 
		
	}
	
}
