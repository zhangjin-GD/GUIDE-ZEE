package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldBirtDept extends MAXTableDomain {

	public FldBirtDept(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDDEPT", "deptnum = :" + thisAttr);
		String[] FromStr = { "deptnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String thisAttr = getMboValue().getAttributeName();
		String personId = mbo.getUserInfo().getPersonId();
		MboSetRemote personSet = mbo.getMboSet("$person", "person", "personId='" + personId + "'");
		if (!personSet.isEmpty() && personSet.count() > 0) {
			MboRemote person = personSet.getMbo(0);
			String udcompany = person.getString("udcompany");
			if ("udcompany".equalsIgnoreCase(thisAttr)) {
				setListCriteria("type = 'COMPANY' and deptnum='" + udcompany + "'");
			} else if ("uddept".equalsIgnoreCase(thisAttr)) {
				setListCriteria("type = 'DEPARTMENT' and parent='" + udcompany + "'");
			} else if ("udofs".equalsIgnoreCase(thisAttr)) {
				setListCriteria("type = 'OFFICE' and parent in (select deptnum from uddept where type = 'DEPARTMENT' "
						+ "and parent in (select deptnum from uddept where type = 'COMPANY' and deptnum='" + udcompany
						+ "'))");
			}
		}
		return super.getList();
	}

}
