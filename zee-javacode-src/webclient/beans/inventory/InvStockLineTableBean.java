package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.inventory.InvStock;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class InvStockLineTableBean extends DataBean {

	public int insertInvStockLine() throws RemoteException, MXException {

		DataBean appBean = this.app.getAppBean();
		InvStock mbo = (InvStock) this.app.getAppBean().getMbo();
		String personid = mbo.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		String invstocknum = mbo.getString("invstocknum");
		boolean isitemzero = mbo.getBoolean("isitemzero");
		String siteid = mbo.getString("siteid");
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}

		MboSetRemote lineSet = mbo.getMboSet("UDINVSTOCKLINE");
		if (!lineSet.isEmpty()) {

			int userInput = MXApplicationYesNoCancelException.getUserInput("check", MXServer.getMXServer(),
					mbo.getUserInfo());
			switch (userInput) {
			case MXApplicationYesNoCancelException.NULL:
				if (mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
					Object[] obj = {
							"Tip: Inventory stocktaking data already exists, whether to refresh the stocktaking data? \n select Yes to continue, select No to cancel!" };
					throw new MXApplicationYesNoCancelException("check", "udmessage", "error0", obj);
				} else {
					Object[] obj = { "提示：已存在盘点数据，是否重新盘点 \n 选择 Yes 继续，选择 No 取消！" };
					throw new MXApplicationYesNoCancelException("check", "udmessage", "error0", obj);
				}
			case MXApplicationYesNoCancelException.YES:
				// 删除
				mbo.invStockLineDelete(invstocknum, siteid);
				// 新增
				mbo.invStockLineInsert(invstocknum, isitemzero, siteid);
				break;
			case MXApplicationYesNoCancelException.NO:
				break;
			case MXApplicationYesNoCancelException.CANCEL:
				break;
			}
		} else {
			/**
			 * ZEE-DJY
			 * 2024-01-30 14:15:17
			 * 库存盘点插入明细行
			 */
			String udcompany = mbo.getString("udcompany");
			if (udcompany.equalsIgnoreCase("ZEE")) {
				mbo.invStockLineInsertZEE(invstocknum, isitemzero, siteid);
			} else {
				// 新增
				mbo.invStockLineInsert(invstocknum, isitemzero, siteid);
			}
			
		}
		mbo.setValue("changeby", personid, 11L);
		mbo.setValue("changetime", currentDate, 11L);
		appBean.save();
		return 1;
	}

	public int updateInvStockQty() throws RemoteException, MXException {
		DataBean appBean = this.app.getAppBean();
		InvStock mbo = (InvStock) this.app.getAppBean().getMbo();
		String personid = mbo.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		String invstocknum = mbo.getString("invstocknum");
		String siteid = mbo.getString("siteid");
		// 更新
		mbo.invStockQtyUpdate(invstocknum, siteid);

		mbo.setValue("changeby", personid, 11L);
		mbo.setValue("changetime", currentDate, 11L);
		appBean.save();
		return 1;
	}
	
}
