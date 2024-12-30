package guide.workflow;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class WFActionCheckAttrsValueC implements ActionCustomClass {

	/**
	 * @param obj:<字段名1,字段名2,提示信息;>
	 * @Description 操作类，验证字段1大于字段2
	 * @return void
	 * @throws MXException
	 * @throws RemoteException
	 * @Date 2014-9-17
	 * @Author FanJi
	 */
	public void applyCustomAction(MboRemote mbo, Object[] obj) throws MXException, RemoteException {

		String attrs = (String) obj[0];
		String[] attr = attrs.split(";");
		double value1 = 0.0;
		for (int i = 0; i < attr.length; i++) {
			value1 = mbo.getDouble(attr[i].split(",")[0]);
			if (value1 <= 0 || value1 < mbo.getDouble(attr[i].split(",")[1])) {
				throw new MXApplicationException("guide", attr[i].split(",")[2]);
			}
		}
	}

}