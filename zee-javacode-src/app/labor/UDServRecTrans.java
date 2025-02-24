package guide.app.labor;

import java.rmi.RemoteException;

import psdi.app.labor.ServRecTrans;
import psdi.app.labor.ServRecTransRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDServRecTrans extends ServRecTrans implements ServRecTransRemote{

	public UDServRecTrans(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	public void init() throws MXException{
		super.init();
		/** 
		 * ZEE - 采购服务接收capex&project-code
		 * 2025-1-24  14:17  
		 */
		try {
			MboRemote owner = getOwner();
			if(owner != null && owner.getString("udcompany").equalsIgnoreCase("ZEE")){
			String ponum = getString("ponum");
			Integer polinenum = getInt("polinenum");
			MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE",MXServer.getMXServer().getSystemUserInfo());
			polineSet.setWhere(" ponum = '" + ponum + "' and polinenum = '"+ polinenum + "' ");
			polineSet.reset();
			if (!polineSet.isEmpty() && polineSet.count() > 0) {
				MboRemote poline = polineSet.getMbo(0);
				String udcosttype = poline.getString("udcosttype").replaceAll("\\s", ""); // 去除所有空格
				String udcapex = getString("udcapex");
				if (udcapex.equalsIgnoreCase("N")) {
					setFieldFlag("udprojectnum", 7L, false); // 取消只读
					setFieldFlag("udprojectnum", 128L, false); // 取消必填
				}else if(udcapex.equalsIgnoreCase("Y") && isNumeric(udcosttype) && Long.parseLong(udcosttype) >= 4000){
					setFieldFlag("udprojectnum", 7L, false); // 取消只读
					setFieldFlag("udprojectnum", 128L, true); // 设置必填
				}
			}
			polineSet.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
     * 检查字符串是否为数字
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
