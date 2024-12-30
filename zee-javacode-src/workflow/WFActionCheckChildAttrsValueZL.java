package guide.workflow;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class WFActionCheckChildAttrsValueZL implements ActionCustomClass {

	/**
	 * @param obj:<关系名,字段名,消息;>
	 * @Description 操作类，验证字段小于0
	 * @return void
	 * @throws MXException
	 * @throws RemoteException
	 * @Date 2014-9-17
	 * @Author FanJi
	 */
	public void applyCustomAction(MboRemote mbo, Object[] obj) throws MXException, RemoteException {

		String attrs = (String) obj[0];
		String[] attr = attrs.split(";");
		double value = 0.0;
		for (int i = 0; i < attr.length; i++) {
			MboSetRemote childSet = mbo.getMboSet(attr[i].split(",")[0]);
			if (childSet != null && childSet.count() > 0) {
				MboRemote child = null;
				for (int j = 0; (child = childSet.getMbo(j)) != null; j++) {
					value = child.getDouble(attr[i].split(",")[1]);
					if (value < 0) {
						throw new MXApplicationException("guide", attr[i].split(",")[2]);
					}
				}
			}

		}

	}

}