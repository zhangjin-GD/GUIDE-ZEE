package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldWOCapex extends MAXTableDomain{

	public UDFldWOCapex(MboValue mbv) {
		super(mbv);
	}
	/** 
	 * ZEE - 工单capex&project-code
	 * 2025-2-18  11:17  
	 */
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		if(mbo != null && mbo instanceof UDWO){
			if(mbo.getString("udcompany").equalsIgnoreCase("ZEE")){
				String udcapex = mbo.getString("udcapex");
				if (udcapex.equalsIgnoreCase("N")) {
					mbo.setValue("udprojectnum", "", 11L);
					mbo.setFieldFlag("udprojectnum", 128L, false); // 取消必填
				}else if(udcapex.equalsIgnoreCase("Y") ){
					mbo.setFieldFlag("udprojectnum", 128L, true); // 设置必填
				}
			}
		}
	}

}
