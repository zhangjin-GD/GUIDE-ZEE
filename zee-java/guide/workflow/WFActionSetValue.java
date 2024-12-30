package guide.workflow;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.util.MXException;

public class WFActionSetValue implements ActionCustomClass {

	/**
	 * @param obj:<字段名,值/:值,action;>
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
		String key = null;
		String value = null;
		for (int i = 0; i < attr.length; i++) {
			key = attr[i].split(",")[0];
			if(key == null || key.equalsIgnoreCase("")){
				key = "status";
			}
			value = attr[i].split(",")[1];
			if(value != null && value.startsWith(":")){
				value = mbo.getString(value.replace(":", ""));
			}
			if(attr[i].split(",")[2].equalsIgnoreCase("1")){
				mbo.setValue(key, value, 2L);
			}else{
				mbo.setValue(key, value, 11L);
			}
		}
		
	}

}