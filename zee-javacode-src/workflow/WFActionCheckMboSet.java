package guide.workflow;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class WFActionCheckMboSet implements ActionCustomClass{

	/**
	 * @param obj:<关系名,列表名;>
	 * @Description 操作类，验证子表是否为空集
	 * @return void
	 * @throws MXException
	 * @throws RemoteException 
	 * @Date 2014-9-17
	 * @Author FanJi
	 */
	public void applyCustomAction(MboRemote mbo, Object[] obj)
			throws MXException, RemoteException {
		
		String relationships= (String) obj[0];
		String[] relationship = relationships.split(";");
		MboSetRemote childSet = null ;
		for (int i = 0; i < relationship.length; i++) {
			childSet = mbo.getMboSet(relationship[i].split(",")[0]);
			if (childSet.isEmpty()) {
				throw new MXApplicationException("guide", relationship[i].split(",")[1]);
			}
		}
		
	}

}