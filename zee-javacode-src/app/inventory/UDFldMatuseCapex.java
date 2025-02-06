package guide.app.inventory;


import guide.app.workorder.UDWO;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldMatuseCapex extends MAXTableDomain{

	public UDFldMatuseCapex(MboValue mbv) {
		super(mbv);
	}
	/** 
	 * ZEE - 工单领料capex&project-code
	 * 2025-1-21  13:17  
	 */
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();//MATUSETRANS
		MboRemote owner = mbo.getOwner();//WO
		if(owner != null && owner instanceof UDWO){
			if(owner.getString("udcompany").equalsIgnoreCase("ZEE")){
				String udcapex = mbo.getString("udcapex");
				String udcosttype = mbo.getString("udcosttype").replaceAll("\\s", ""); // 去除所有空格
				if (udcapex.equalsIgnoreCase("N")) {
					mbo.setValue("udprojectnum", "", 11L);
					mbo.setFieldFlag("udprojectnum", 128L, false); // 取消必填
				}else if(udcapex.equalsIgnoreCase("Y") && isNumeric(udcosttype) &&  Long.parseLong(udcosttype) < 4000){
					mbo.setFieldFlag("udprojectnum", 128L, true); // 设置必填
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
