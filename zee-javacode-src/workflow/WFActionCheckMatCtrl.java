package guide.workflow;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class WFActionCheckMatCtrl implements ActionCustomClass {


	public void applyCustomAction(MboRemote mbo, Object[] obj) throws MXException, RemoteException {

		String attrs = (String) obj[0];
		String[] attr = attrs.split(",");
		String relationName = attr[0];
		String ctrlAttr = attr[1];
		String actAttr = attr[2];
		String deptName = attr[3];
		MboSetRemote objectSet = mbo.getMboSet(relationName);
		if(!objectSet.isEmpty() && objectSet.count() > 0) {
			MboRemote object = null;
			for (int i = 0; (object = objectSet.getMbo(i)) != null; i++) {
				if(object.getDouble(actAttr) > object.getDouble(ctrlAttr)){
					Object params[] = { "提示："+object.getString(deptName)+"设置金额"+object.getDouble(ctrlAttr)+"实际使用"+object.getDouble(actAttr)+"，已超过控制金额！" };
					throw new MXApplicationException("instantmessaging", "tsdimexception", params);
				}
			}
		}
		
	}

}