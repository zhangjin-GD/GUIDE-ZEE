package guide.app.labor;

import guide.app.po.UDPO;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldServrecCapex extends MAXTableDomain{

	public UDFldServrecCapex(MboValue mbv) {
		super(mbv);
	}
	/** 
	 * ZEE - 采购服务接收capex&project-code
	 * 2025-1-26  10:17  
	 */
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();//POLINE
		MboRemote owner = mbo.getOwner();//PO
		if(owner != null && owner instanceof UDPO){
			if(owner.getString("udcompany").equalsIgnoreCase("ZEE")){
				String udcapex = mbo.getString("udcapex");
				String ponum = mbo.getString("ponum");
				Integer polinenum = mbo.getInt("polinenum");
				MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
				polineSet.setWhere(" ponum = '" + ponum + "' and polinenum = '"+ polinenum+"' ");
				polineSet.reset();
				if(!polineSet.isEmpty() && polineSet.count() > 0){
					MboRemote poline = polineSet.getMbo(0);
					String udcosttype = poline.getString("udcosttype").replaceAll("\\s", ""); // 去除所有空格
					if (udcapex.equalsIgnoreCase("N")) {
						mbo.setValue("udprojectnum", "", 11L);
						mbo.setFieldFlag("udprojectnum", 7L, false); // 取消只读
						mbo.setFieldFlag("udprojectnum", 128L, false); // 取消必填
					}else if(udcapex.equalsIgnoreCase("Y") && isNumeric(udcosttype) &&  Long.parseLong(udcosttype) < 4000){
						mbo.setValue("udprojectnum", "", 11L);
						mbo.setFieldFlag("udprojectnum", 128L, false); // 设置非必填
						mbo.setFieldFlag("udprojectnum", 7L, true); // 设置只读
					}else if(udcapex.equalsIgnoreCase("Y") && isNumeric(udcosttype) && Long.parseLong(udcosttype) >= 4000){
						mbo.setFieldFlag("udprojectnum", 7L, false); // 取消只读
						mbo.setFieldFlag("udprojectnum", 128L, true); // 设置必填
					}
				}
			}
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
