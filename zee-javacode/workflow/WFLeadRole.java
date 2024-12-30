package guide.workflow;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.common.role.CustomRoleAdapter;
import psdi.common.role.CustomRoleInterface;
import psdi.common.role.MaxRole;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class WFLeadRole extends CustomRoleAdapter implements CustomRoleInterface {

	public WFLeadRole() {
	}

	public MboRemote evaluateCustomRole(MaxRole roleMbo, MboRemote currentMbo) throws MXException, RemoteException {
		String condition = "1 = 1";
		String param = roleMbo.getString("parameter");
		if(param != null && !param.equalsIgnoreCase(""))
			condition = "description like '"+param+"%'";
		String dept = currentMbo.getString("uddept");
		MboSetRemote persongroupsSet = roleMbo.getMboSet("$PERSONGROUP", "PERSONGROUP", " "+ condition +" start with uddept = '" + dept + "' and description like '%部门经理%' connect by prior parent = persongroup");
		if(!persongroupsSet.isEmpty() && persongroupsSet.count() > 0){
			return persongroupsSet.getMbo(0);
		}
		return CommonUtil.getWFAdmin(roleMbo);
	}

}
