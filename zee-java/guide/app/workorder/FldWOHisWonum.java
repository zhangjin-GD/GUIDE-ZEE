package guide.app.workorder;

import java.rmi.RemoteException;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWOHisWonum extends MAXTableDomain {

	public FldWOHisWonum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("workorder", "wonum =:" + thisAttr);
		String[] FromStr = { "wonum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("worktype =:worktype and udassettypecode =:udassettypecode and status ='COMP'");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		if (!this.getMboValue().isNull()) {
			MboSetRemote hisWoSet = mbo.getMboSet("UDHISWONUM");
			if (hisWoSet != null && !hisWoSet.isEmpty()) {
				MboRemote hisWo = hisWoSet.getMbo(0);
				String assetnum = hisWo.getString("assetnum");// 事件分类
				String udwoanalysis = hisWo.getString("udwoanalysis");// 事件分类
				String udfailmech = hisWo.getString("udfailmech");// 故障机构
				String udfailparts = hisWo.getString("udfailparts");// 故障部件
				String udfailtype = hisWo.getString("udfailtype");// 故障类别
				String udfailcause = hisWo.getString("udfailcause");// 故障原因
				String udfailremedy = hisWo.getString("udfailremedy");// 解决方案
				String udfailanalysis = hisWo.getString("udfailanalysis");// 故障分析
				String jpnum = hisWo.getString("jpnum");// JP

				mbo.setValue("assetnum", assetnum, 2L);
				mbo.setValue("udwoanalysis", udwoanalysis, 11L);
				mbo.setValue("udfailmech", udfailmech, 11L);
				mbo.setValue("udfailparts", udfailparts, 11L);
				mbo.setValue("udfailtype", udfailtype, 11L);
				mbo.setValue("udfailcause", udfailcause, 11L);
				mbo.setValue("udfailremedy", udfailremedy, 11L);
				mbo.setValue("udfailanalysis", udfailanalysis, 11L);
				mbo.setValue("jpnum", jpnum, 2L);
			}
		}
	}
}
