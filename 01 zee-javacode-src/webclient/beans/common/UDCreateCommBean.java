package guide.webclient.beans.common;

import guide.app.po.UDPO;
import guide.app.po.UDWFZEESendMailAction;


import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXException;
import psdi.webclient.beans.common.CreateCommBean;

/**
 *@function:创建通信按钮-初始化值
 *@author:zj
 *@date:2024-06-11 09:13:06
 *@modify:
 */
public class UDCreateCommBean extends CreateCommBean {
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo != null && mbo instanceof UDPO) {
			String udcompany = mbo.getString("udcompany");
			String templateid = "1004"; //ZEE-PO模板编号
			if (udcompany != null && udcompany.equalsIgnoreCase("ZEE")) {
				//更新附件
				UDWFZEESendMailAction.updateTemplateDoc(mbo);
				
				//带模板
				setValue("templateid", templateid, 2L);
			}
		}
	}

}
