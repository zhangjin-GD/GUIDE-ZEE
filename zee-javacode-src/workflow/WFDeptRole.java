package guide.workflow;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.common.role.CustomRoleAdapter;
import psdi.common.role.CustomRoleInterface;
import psdi.common.role.MaxRole;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class WFDeptRole extends CustomRoleAdapter implements CustomRoleInterface {

	public WFDeptRole() {
	}

	public MboRemote evaluateCustomRole(MaxRole roleMbo, MboRemote currentMbo) throws MXException, RemoteException {
		String condition = "1 = 1";
		String param = roleMbo.getString("parameter");
		if(param != null && !param.equalsIgnoreCase(""))
			condition = "description like '"+param+"%'";
		String dept = currentMbo.getString("uddept");
		MboSetRemote persongroupsSet = roleMbo.getMboSet("$PERSONGROUP", "PERSONGROUP", "uddept='" + dept + "' and "+ condition +"");
		if(!persongroupsSet.isEmpty() && persongroupsSet.count() > 0){
			return persongroupsSet.getMbo(0);
		}
		return CommonUtil.getWFAdmin(roleMbo);
	}

}
