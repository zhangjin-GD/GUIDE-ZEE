package guide.webclient.beans.pr;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;
import psdi.webclient.beans.pr.PRLinesTableBean;

public class UDPRLinesTableBean extends PRLinesTableBean {

	public int udprimp() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		String matStatus = mbo.getString("udmatstatus");
		String matDesc = mbo.getString("udmatstatus.description");
		int flag = 2;
		if (!"MONTHLY".equalsIgnoreCase(matStatus)) {
			int userInput;
			userInput = MXApplicationYesNoCancelException.getUserInput("check", MXServer.getMXServer(),
					mbo.getUserInfo());
			switch (userInput) {
			case MXApplicationYesNoCancelException.NULL:
				if (mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
					Object[] obj = { "Tips: This document is " + matDesc
							+ ", please select the corresponding button! \n Select Yes to continue or No to cancel!" };
					throw new MXApplicationYesNoCancelException("check", "udmessage", "error0", obj);
				} else {
					Object[] obj = { "温馨提示：该单据为 " + matDesc + "，请选择对应按钮！ \n 选择 Yes 继续，选择 No 取消！" };
					throw new MXApplicationYesNoCancelException("check", "udmessage", "error0", obj);
				}
			case MXApplicationYesNoCancelException.YES:
				flag = 2;
				break;
			case MXApplicationYesNoCancelException.NO:
				flag = 1;
				break;
			case MXApplicationYesNoCancelException.CANCEL:
				flag = 1;
				break;
			}
		}
		return flag;
	}

	public int uditems() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		String matStatus = mbo.getString("udmatstatus");
		String matDesc = mbo.getString("udmatstatus.description");
		int flag = 2;
		if ("MONTHLY".equalsIgnoreCase(matStatus)) {
			int userInput;
			userInput = MXApplicationYesNoCancelException.getUserInput("check", MXServer.getMXServer(),
					mbo.getUserInfo());
			switch (userInput) {
			case MXApplicationYesNoCancelException.NULL:
				if (mbo.getUserInfo().getLangCode().equalsIgnoreCase("en")) {
					Object[] obj = { "Tips: This document is " + matDesc
							+ ", please select the corresponding button! \n Select Yes to continue or No to cancel!" };
					throw new MXApplicationYesNoCancelException("check", "udmessage", "error0", obj);
				} else {
					Object[] obj = { "温馨提示：该单据为 " + matDesc + "，请选择对应按钮！ \n 选择 Yes 继续，选择 No 取消！" };
					throw new MXApplicationYesNoCancelException("check", "udmessage", "error0", obj);
				}
			case MXApplicationYesNoCancelException.YES:
				flag = 2;
				break;
			case MXApplicationYesNoCancelException.NO:
				flag = 1;
				break;
			case MXApplicationYesNoCancelException.CANCEL:
				flag = 1;
				break;
			}
		}
		return flag;
	}

	// pr 重新编号
	public int cxpx() throws RemoteException, MXException {
		int j = 1;
		MboRemote mbo = app.getAppBean().getMbo();
		MboSetRemote lineSet = mbo.getMboSet("PRLINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			for (int i = 0; lineSet.getMbo(i) != null; i++) {
				MboRemote line = lineSet.getMbo(i);
				line.setValue("prlinenum", j++, 2L);
			}
		}
		this.refreshTable();
		return 1;
	}
}
