package guide.workflow;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.util.MXException;

public class WFActionSetValueNull implements ActionCustomClass {

	/**
	 * @param obj:<字段名,属性;,action;>
	 * @Description 操作类，设置值
	 * @return void
	 * @throws MXException
	 * @throws RemoteException
	 * @Date 2014-9-17
	 * @Author FanJi
	 */
	public void applyCustomAction(MboRemote mbo, Object[] obj) throws MXException, RemoteException {

		String attrs = (String) obj[0];
		String[] attr = attrs.split(";");
		String key = "";
		for (int i = 0; i < attr.length; i++) {
			key = attr[i].split(",")[0];
			if (!key.isEmpty()) {
				if (attr[i].split(",")[1].equalsIgnoreCase("1")) {
					mbo.setValueNull(key, 2L);
				} else {
					mbo.setValueNull(key, 11L);
				}
			}
		}
	}
}