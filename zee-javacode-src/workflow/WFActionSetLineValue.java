package guide.workflow;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class WFActionSetLineValue implements ActionCustomClass {

	/**
	 * @param obj:<关系,字段名,值/:值,action;>
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
		String relation = null;
		String key = null;
		String value = null;
		for (int i = 0; i < attr.length; i++) {
			relation = attr[i].split(",")[0];
			key = attr[i].split(",")[1];
			if(key == null || key.equalsIgnoreCase("")){
				key = "description";
			}
			value = attr[i].split(",")[2];
			if(value != null && value.startsWith(":")){
				value = mbo.getString(value.replace(":", ""));
			}
			MboSetRemote lineMboSet = mbo.getMboSet(relation); 
			if(!lineMboSet.isEmpty() && lineMboSet.count() > 0){
				MboRemote lineMbo = null;
				for (int j = 0; (lineMbo = lineMboSet.getMbo(j)) != null; j++) {
					if(attr[i].split(",")[3].equalsIgnoreCase("1")){
						lineMbo.setValue(key, value, 2L);
					}else{
						lineMbo.setValue(key, value, 11L);
					}
				}
			}
			
		}
		
	}

}