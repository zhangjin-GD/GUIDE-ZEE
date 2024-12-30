package guide.workflow;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class WFActionCheckAttrsValue implements ActionCustomClass {

	/**
	 * @param obj:<字段名,值,提示信息;>
	 * @Description 操作类，字段等于值
	 * @return void
	 * @throws MXException
	 * @throws RemoteException
	 * @Date 2014-9-17
	 * @Author FanJi
	 */
	public void applyCustomAction(MboRemote mbo, Object[] obj) throws MXException, RemoteException {

		String attrs = (String) obj[0];
		String[] attr = attrs.split(";");
		String value = null;
		for (int i = 0; i < attr.length; i++) {
			value = mbo.getString(attr[i].split(",")[0]);
			if (value != null && attr[i].split(",")[1].equalsIgnoreCase(value)) {
				throw new MXApplicationException("guide", attr[i].split(",")[2]);
			}
		}
	}

}