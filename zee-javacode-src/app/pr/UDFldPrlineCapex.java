package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldPrlineCapex extends MAXTableDomain{

	public UDFldPrlineCapex(MboValue mbv) {
		super(mbv);
	}
	/** 
	 * ZEE - 采购申请capex&project-code
	 * 2025-1-21  13:17  
	 */
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();//PRLINE
		MboRemote owner = mbo.getOwner();//PR
		if(owner != null && owner instanceof UDPR){
			if(owner.getString("udcompany").equalsIgnoreCase("ZEE")){
				String udcapex = mbo.getString("udcapex");
				String udcosttype = mbo.getString("udcosttype").replaceAll("\\s", ""); // 去除所有空格
				if (udcapex.equalsIgnoreCase("N")) {
					mbo.setValue("udprojectnum", "", 11L);
					mbo.setFieldFlag("udprojectnum", 7L, false); // 取消只读
					mbo.setFieldFlag("udprojectnum", 128L, false); // 取消必填
				}else if(udcapex.equalsIgnoreCase("Y") && isNumeric(udcosttype)  && Long.parseLong(udcosttype) < 4000){
					mbo.setValue("udprojectnum", "", 11L);
					mbo.setFieldFlag("udprojectnum", 128L, false); // 设置非必填
					mbo.setFieldFlag("udprojectnum", 7L, true); // 设置只读
				}else if(udcapex.equalsIgnoreCase("Y") && isNumeric(udcosttype) && Long.parseLong(udcosttype) >= 4000){
					mbo.setFieldFlag("udprojectnum", 7L, false); // 取消只读
					mbo.setFieldFlag("udprojectnum", 128L, true); // 设置必填
					mbo.setValue("issue", "Y", 2L);
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
