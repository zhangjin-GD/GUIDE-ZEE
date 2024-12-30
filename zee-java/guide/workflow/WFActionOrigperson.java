package guide.workflow;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class WFActionOrigperson implements ActionCustomClass {

	/**
	 * @param obj:<字段名,描述;>
	 * @Description 操作类，验证字段是否填写
	 * @return void
	 * @throws MXException
	 * @throws RemoteException
	 * @Date 2014-9-17
	 * @Author FanJi
	 */
	public void applyCustomAction(MboRemote mbo, Object[] obj) throws MXException, RemoteException {

		String origperson = (String) obj[0];
		if (!mbo.getUserName().equalsIgnoreCase(mbo.getString(origperson))) {
			throw new MXApplicationException("guide", "1116");
		}

	}

}