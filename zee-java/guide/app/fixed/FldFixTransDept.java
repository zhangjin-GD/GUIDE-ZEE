package guide.app.fixed;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldFixTransDept extends MAXTableDomain {

	public FldFixTransDept(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDDEPT", "deptnum = :" + thisAttr);
		String[] FromStr = { "deptnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "type='DEPARTMENT' and parent=:udcompany";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		
		String thisAttr = getMboValue().getAttributeName();
		MboRemote mbo = this.getMboValue().getMbo();
		if ("transferindept".equalsIgnoreCase(thisAttr)) {
			mbo.setValueNull("transferinadmin", 11L);
			mbo.setValueNull("transferinlead", 11L);
		} else if ("calloutdept".equalsIgnoreCase(thisAttr)) {
			mbo.setValueNull("calloutadmin", 11L);
			mbo.setValueNull("calloutlead", 11L);
		}
		String dept = this.getMboValue().getString();
		MboSetRemote personSet = mbo.getMboSet("$DEPTMGR", "PERSON", "personid in(select resppartygroup from persongroup,persongroupteam"
				+ " where persongroup.persongroup=persongroupteam.persongroup and persongroup.uddept='"+dept+"' and persongroup.description like '部门经理%')");
		if(!personSet.isEmpty() && personSet.count() > 0){
			if ("transferindept".equalsIgnoreCase(thisAttr)) {
				mbo.setValue("transferinlead", personSet.getMbo(0).getString("personid"), 11L);
			} else if ("calloutdept".equalsIgnoreCase(thisAttr)) {
				mbo.setValue("calloutlead", personSet.getMbo(0).getString("personid"), 11L);
			}
		}
		
		MboSetRemote perTitSet = mbo.getMboSet("$PERSON", "PERSON", "uddept='"+dept+"' and title like '%固定资产管理员%'");
		if (!perTitSet.isEmpty() && perTitSet.count() > 0) {
			if ("transferindept".equalsIgnoreCase(thisAttr)) {
				mbo.setValue("transferinadmin", perTitSet.getMbo(0).getString("personid"), 11L);
			} else if ("calloutdept".equalsIgnoreCase(thisAttr)) {
				mbo.setValue("calloutadmin", perTitSet.getMbo(0).getString("personid"), 11L);
			}
		}
	}
	
}
