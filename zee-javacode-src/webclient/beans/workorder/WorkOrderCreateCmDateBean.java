package guide.webclient.beans.workorder;

import java.rmi.RemoteException;

import guide.app.workorder.UDWO;
import guide.app.workorder.UDWOSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class WorkOrderCreateCmDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		UDWO owner = (UDWO) this.app.getAppBean().getMbo();
		MboSetRemote thisMboSet = owner.getMboSet("UDTHISWO");
		if (thisMboSet != null && !thisMboSet.isEmpty()) {
			MboRemote thisMbo = thisMboSet.getMbo(0);
			String orireason = thisMbo.getString("udvorireason");// 转单原因
			if (orireason != null && !orireason.equalsIgnoreCase("")) {
				UDWOSet oriWoSet = (UDWOSet) owner.getMboSet("UDORIWO");
				if (!oriWoSet.isEmpty() && oriWoSet.count() > 0) {
					MboRemote oriWo = oriWoSet.getMbo(0);
					String wonum = oriWo.getString("wonum");
					Object[] obj = { wonum };
					throw new MXApplicationException("guide", "1045", obj);
				} else {
					createWoCm(thisMbo);
				}
			} else {
				throw new MXApplicationException("guide", "1046");
			}
		}
		owner.setValue("changedate", MXServer.getMXServer().getDate());
		return super.execute();
	}

	private void createWoCm(MboRemote thisMbo) throws RemoteException, MXException {
		MboSetRemote woSet = MXServer.getMXServer().getMboSet("WORKORDER", thisMbo.getUserInfo());
		woSet.setWhere("1=2");
		MboRemote oriWo = woSet.add();
		oriWo.setValue("description", thisMbo.getString("description"), 11L);
		oriWo.setValue("assetnum", thisMbo.getString("assetnum"), 2L);
		oriWo.setValue("targstartdate", MXServer.getMXServer().getDate(), 2L);
		oriWo.setValue("actstart", MXServer.getMXServer().getDate(), 2L);
		oriWo.setValue("udoriwonum", thisMbo.getString("wonum"), 11L);
		oriWo.setValue("worktype", "CM", 2L);
		oriWo.setValue("udorireason", thisMbo.getString("udvorireason"), 11L);
		oriWo.setValue("udremark", thisMbo.getString("udvremark"), 11L);
		oriWo.setValue("udfailasset", thisMbo.getString("udfailasset"), 11L);
		oriWo.setValue("udfailmech", thisMbo.getString("udfailmech"), 11L);// 故障机构
		oriWo.setValue("udfailparts", thisMbo.getString("udfailparts"), 11L);
		oriWo.setValue("udfailtype", thisMbo.getString("udfailtype"), 11L);// 故障类别
		oriWo.setValue("udfailproblem", thisMbo.getString("udfailproblem"), 11L);// 故障现象
		oriWo.setValue("udfailcause", thisMbo.getString("udfailcause"), 11L);// 故障原因
		oriWo.setValue("udfailcausedesc", thisMbo.getString("udfailcausedesc"), 11L);// 故障原因
		oriWo.setValue("udfailremedy", thisMbo.getString("udfailremedy"), 11L);// 解决方案
		oriWo.setValue("udfailremedydesc", thisMbo.getString("udfailremedydesc"), 11L);// 解决方案
		oriWo.setValue("udfailanalysis", thisMbo.getString("udfailanalysis"), 11L);// 故障分析
		woSet.save();
		String wonum = oriWo.getString("wonum");
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", "工单创建成功，单号:" + wonum + "", 1);
	}
}
