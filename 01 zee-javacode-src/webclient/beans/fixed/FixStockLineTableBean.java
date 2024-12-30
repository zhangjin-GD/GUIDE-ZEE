package guide.webclient.beans.fixed;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.fixed.FixStock;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class FixStockLineTableBean extends DataBean {

	public void insertFixStockLine() throws RemoteException, MXException {

		DataBean appBean = this.app.getAppBean();
		FixStock mbo = (FixStock) this.app.getAppBean().getMbo();
		String personid = mbo.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		String fixstocknum = mbo.getString("fixstocknum");
		String udcompany = mbo.getString("udcompany");
		String siteid = mbo.getString("siteid");
		String orgid = mbo.getString("orgid");
		if (toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}

		MboSetRemote lineSet = mbo.getMboSet("UDFIXSTOCKLINE");
		if (!lineSet.isEmpty()) {
			int userInput = MXApplicationYesNoCancelException.getUserInput("check", MXServer.getMXServer(),
					mbo.getUserInfo());
			switch (userInput) {
			case MXApplicationYesNoCancelException.NULL:
				Object[] obj = { "温馨提示：已存在盘点数据，是否重新盘点 \n 选择 Yes 继续，选择 No 取消！" };
				throw new MXApplicationYesNoCancelException("check", "udmessage", "error0", obj);
			case MXApplicationYesNoCancelException.YES:
				// 删除
				mbo.fixStockLineDelete(fixstocknum, siteid, orgid);
				// 新增
				mbo.fixStockLineInsert(fixstocknum, udcompany, siteid, orgid);
				break;
			case MXApplicationYesNoCancelException.NO:
				break;
			case MXApplicationYesNoCancelException.CANCEL:
				break;
			}
		} else {
			// 新增
			mbo.fixStockLineInsert(fixstocknum, udcompany, siteid, orgid);
		}
		mbo.setValue("changeby", personid, 11L);
		mbo.setValue("changetime", currentDate, 11L);
		appBean.save();
	}
}
