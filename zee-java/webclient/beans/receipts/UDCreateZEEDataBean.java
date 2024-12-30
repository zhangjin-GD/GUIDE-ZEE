package guide.webclient.beans.receipts;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.webclient.system.session.WebClientSession;

/**
 * ZEE-从物资创建ASSET
 * @author djy
 *2024-05-11 10:47:43
 */

public class UDCreateZEEDataBean extends DataBean {
	
	public synchronized int execute() throws MXException, RemoteException{
		MboRemote poowner = this.app.getAppBean().getMbo();
		String udcompany = poowner.getString("udcompany");
		if (!udcompany.isEmpty() && udcompany.equalsIgnoreCase("ZEE") ){
		DataBean db = app.getDataBean("main_matreceiptstable");
		MboRemote mbo = db.getMbo(db.getCurrentRow());
		String udasset = getString("udasset");
		String udassetname = getString("udassetname");
		String udcostcenterasset = getString("udcostcenterasset");
		String linecost = getString("linecost");
		String udassettypecode = getString("udassettypecode");
		String itemnum = mbo.getString("itemnum");
		String udnature = getString("udnature");
		String uddept = getString("uddept");
		String udwogroup = getString("udwogroup");
		int polineid = mbo.getInt("poline.polineid");

		int userInput = MXApplicationYesNoCancelException.getUserInput("UDCREATEASSET", MXServer.getMXServer(),mbo.getUserInfo());
		switch (userInput) {
		case 16:// NO 如果选择否，则执行16下面的逻辑
			break;
		case 8: // YES 如果选择是，则执行8下面的逻辑
			MboSetRemote assetSet = MXServer.getMXServer().getMboSet("ASSET",MXServer.getMXServer().getSystemUserInfo());
			assetSet.setWhere("assetnum='" + udasset + "'");
			assetSet.reset();
			if (assetSet.isEmpty()) {
				MboRemote asset = assetSet.add(11L);
				asset.setValue("assetnum", udasset, 11L);
				asset.setValue("description", udassetname, 11L);
				asset.setValue("uditemnum", itemnum, 11L);
				asset.setValue("udcompany", udcompany, 11L);
				asset.setValue("udpolineid", polineid, 2L);
				asset.setValue("udcostcenter", udcostcenterasset, 2L);
				asset.setValue("udassettypecode", udassettypecode, 2L);
				asset.setValue("purchaseprice", linecost, 2L);	
				asset.setValue("udnature", udnature, 2L);	
				asset.setValue("uddept", uddept, 2L);	
				asset.setValue("udlocation", "LOCZEE", 2L);
				asset.setValue("udwogroup", udwogroup, 2L);
				assetSet.save();
				assetSet.close();
				clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "",
						"The Asset created: "+  udasset  +", please complete the remained asset information  as soon as possible！",
						1);
				String appname = "UDASSET";
				 WebClientEvent event = this.clientSession.getCurrentEvent();
		         if (event != null) {
		             String value = event.getValueString();
		             if (value != null) {
							super.execute();
							// 获取系统session实例
							WebClientSession wcs = sessionContext
									.getMasterInstance();
							// 构建跳转至启动中心的URL
							String url = "?event=loadapp&value=" + appname
									+ "&uniqueid=" + asset.getInt("ASSETUID")
									+ "";
							// 跳转动作执行
							wcs.gotoApplink(url);
		             }
		     }//确认后，跳转至单据
			break; // 客户化逻辑需要加入break，否则弹出框会弹出两次
			} else {
				Object[] obj = { "The Asset already exists. Do not create it again!" };
				throw new MXApplicationException("udmessage", "error1", obj);
			}
		case -1: // 首先进入（点击选择操作里的“自定义删除”），弹出选择框
			throw new MXApplicationYesNoCancelException("UDCREATEASSET", "UDCREATEASSET", "UDCREATEASSET", new String[] {});
		}
		}
		return currentRow;	
	}
}
