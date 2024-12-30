package guide.app.company;

import java.util.Date;
import java.util.List;
import java.util.Map;
import guide.app.common.CommonUtil;
import guide.iface.mdm.webservice.MdmWebService;
import guide.iface.mdm.webservice.ReturnBean;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;

public class UDCompanyCronTask extends SimpleCronTask {

	@Override
	public void cronAction() {
		try {
			getMdm();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取MDM接口数据
	 * 
	 * @throws Exception
	 */
	private void getMdm() throws Exception {
		MXServer server = MXServer.getMXServer();
		Date sysdate = server.getDate();
		String sysDateStr = CommonUtil.getDateFormat(sysdate, "yyyy-mm-dd");
		ReturnBean result = MdmWebService.itemRequestWebService(null, sysDateStr);
		List<Map<String, Object>> list = result.getList();
		if (list.size() > 0) {
			// 返回供商商集合数据解析
			for (Map<String, Object> map : list) {
				String name = map.get("vENDOR_NAME").toString();// 供应商编号
				UDCompanySet compSet = (UDCompanySet) server.getMboSet("COMPANIES", server.getSystemUserInfo());
				compSet.setWhere("name='" + name + "'");
				compSet.reset();
				if (compSet != null && !compSet.isEmpty()) {
					updateCompany(compSet, map);
				} else {
					insetCompany(compSet, map);
				}
				compSet.save();
				compSet.close();
			}

		}
	}

	/**
	 * 新增供应商信息
	 * 
	 * @param compSet
	 * @param map
	 * @throws Exception
	 */
	private void insetCompany(UDCompanySet compSet, Map<String, Object> map) throws Exception {
		UDCompany comp = (UDCompany) compSet.add();
		comp.setCompToMdm(map, "ADD");
	}

	/**
	 * 更新供应商信息
	 * 
	 * @param compSet
	 * @param map
	 * @throws Exception
	 */
	private void updateCompany(MboSetRemote compSet, Map<String, Object> map) throws Exception {
		UDCompany comp = (UDCompany) compSet.getMbo(0);
		comp.setCompToMdm(map, "UPP");
	}

}
