package guide.webclient.beans.company;

import java.util.List;
import java.util.Map;

import guide.app.company.UDCompany;
import guide.iface.mdm.webservice.MdmWebService;
import guide.iface.mdm.webservice.ReturnBean;
import psdi.webclient.system.beans.AppBean;
import psdi.webclient.system.controller.Utility;

public class CompanyAppBean extends AppBean {

	public void UPCOMPTOMDM() throws Exception {
		UDCompany comp = (UDCompany) this.getMbo();
		String company = comp.getString("udmdmnum");
		if (company != null && !company.isEmpty()) {
			ReturnBean result = MdmWebService.itemRequestWebService(company, null);
			List<Map<String, Object>> list = result.getList();
			if (list.size() > 0) {
				// 返回供商商集合数据解析
				for (Map<String, Object> map : list) {
					comp.setCompToMdm(map, "UPP");
					Utility.showMessageBox(this.sessionContext.getCurrentEvent(), "guide", "1039", null);
				}
			} else {
				Utility.showMessageBox(this.sessionContext.getCurrentEvent(), "guide", "1027", null);
			}
		} else {
			Utility.showMessageBox(this.sessionContext.getCurrentEvent(), "guide", "1027", null);
		}
		this.app.getAppBean().save();
	}
}
