package guide.workflow;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.common.role.CustomRoleAdapter;
import psdi.common.role.CustomRoleInterface;
import psdi.common.role.MaxRole;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class WFWorkPersongroup extends CustomRoleAdapter implements CustomRoleInterface {

	public WFWorkPersongroup() {
	}

	public MboRemote evaluateCustomRole(MaxRole roleMbo, MboRemote currentMbo) throws MXException, RemoteException {
		MboSetRemote persongroupsSet = currentMbo.getMboSet("$PERSONGROUP", "PERSONGROUP", "persongroup = :persongroup");
		if(!persongroupsSet.isEmpty() && persongroupsSet.count() > 0){
			return persongroupsSet.getMbo(0);
		}
		return CommonUtil.getWFAdmin(roleMbo);
	}

}
