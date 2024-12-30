package guide.workflow.virtual;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
import psdi.workflow.virtual.FldCompleteActionId;

public class UDFldCompleteActionId extends FldCompleteActionId {

	public UDFldCompleteActionId(MboValue mbovalue) {
		super(mbovalue);
	}

	public void init() throws MXException, RemoteException {
		super.init();
		MboRemote mbo = getMboValue().getMbo();
		String langCode = mbo.getUserInfo().getLangCode();
		if(langCode != null && langCode.equalsIgnoreCase("ZH")){
			mbo.setValue("memo", "同意", 11L);
		}else{
			mbo.setValue("memo", "Agree", 11L);
		}
		mbo.setFieldFlag("memo", 128L, true);
	}

	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();//completewf
		MboRemote owner = mbo.getOwner();//wfassignment
		String processName = owner.getString("processname");
		int processRev = owner.getInt("processrev");
		int actionId = mbo.getInt("actionid");
		String sql = "processname='"+processName+"' and processrev="+processRev+" and actionid="+actionId;
		MboSetRemote wfactionSet = mbo.getMboSet("$WFACTION", "WFACTION", sql);//wfaction
		if(!wfactionSet.isEmpty() && wfactionSet.count()>0){
			boolean ispositive = wfactionSet.getMbo(0).getBoolean("ispositive");
			if (ispositive)
			{
				mbo.setFieldFlag("memo", 128L, true);
				mbo.setValue("memo", "同意",11L);
			} else {
				mbo.setFieldFlag("memo", 128L, true);
				mbo.setValueNull("memo", 11L);
			}
		}
	}

}