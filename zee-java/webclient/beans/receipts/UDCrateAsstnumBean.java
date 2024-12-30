package guide.webclient.beans.receipts;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

/**
 * @function:
 * @date:2021-03-01 15:47:43
 * @modify:
 */
public class UDCrateAsstnumBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote poowner = this.app.getAppBean().getMbo();
		String udcompany = poowner.getString("udcompany");
		if (!udcompany.isEmpty() && udcompany.equalsIgnoreCase("GR02PCT")) {
			DataBean db = app.getDataBean("main_matreceiptstable");
			MboRemote mbo = db.getMbo(db.getCurrentRow());
			String udasset = getString("udasset");
			String udassetname = getString("udassetname");
			String itemnum = mbo.getString("itemnum");
			int polineid = mbo.getInt("poline.polineid");
			int userInput = MXApplicationYesNoCancelException.getUserInput("UDADDSBSS", MXServer.getMXServer(),
					mbo.getUserInfo());
			switch (userInput) {
			case 16:// NO 如果选择否，则执行16下面的逻辑
				break;
			case 8: // YES 如果选择是，则执行8下面的逻辑
				MboSetRemote assetSet = MXServer.getMXServer().getMboSet("ASSET",
						MXServer.getMXServer().getSystemUserInfo());
				assetSet.setWhere("assetnum='" + udasset + "'");
				assetSet.reset();
				if (assetSet.isEmpty()) {
					MboRemote asset = assetSet.add();
					asset.setValue("assetnum", udasset, 11L);
					asset.setValue("description", udassetname, 11L);
					asset.setValue("uditemnum", itemnum, 11L);
					asset.setValue("udcompany", udcompany, 11L);
					asset.setValue("udpolineid", polineid, 2L);
					assetSet.save();
					assetSet.close();
					clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "",
							"After the device and facility are created, complete the device information in the device and facility as soon as possible！",
							1);
					// this.app.getAppBean().save();
					// this.app.getAppBean().refreshTable();
					// this.app.getAppBean().reloadTable();
					break; // 客户化逻辑需要加入break，否则弹出框会弹出两次
				} else {
					Object[] obj = { "The device code already exists. Do not create it again!" };
					throw new MXApplicationException("udmessage", "error1", obj);
				}

			case -1: // 首先进入（点击选择操作里的“自定义删除”），弹出选择框
				throw new MXApplicationYesNoCancelException("UDADDSBSS", "UDADDSBSS", "UDADDSBSS", new String[] {});
			}
		}
		return currentRow;
	}
}