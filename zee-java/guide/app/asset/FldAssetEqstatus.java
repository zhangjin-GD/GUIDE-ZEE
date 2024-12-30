package guide.app.asset;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldAssetEqstatus extends MboValueAdapter {

	public FldAssetEqstatus(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		
		MboRemote mbo = this.getMboValue().getMbo();
		String eqStatus = "INTACT";
		String eqStatusDesc = "完好";
		String langCode = mbo.getUserInfo().getLangCode();
		
		MboSetRemote woStopSet = mbo.getMboSet("EQISSTOP");
		if (!woStopSet.isEmpty() && woStopSet.count() > 0) {
			eqStatus = "STOP";
		}

		MboSetRemote woImSet = mbo.getMboSet("EQISIM");
		if (!woImSet.isEmpty() && woImSet.count() > 0) {
			eqStatus = "IM";
		}
		
		MboSetRemote woPmSet = mbo.getMboSet("EQISPM");
		if (!woPmSet.isEmpty() && woPmSet.count() > 0) {
			eqStatus = "PM";
		}
		
		MboSetRemote woCmSet = mbo.getMboSet("EQISCM");
		if (!woCmSet.isEmpty() && woCmSet.count() > 0) {
			eqStatus = "CM";
		}
		
		MboSetRemote woEmSet = mbo.getMboSet("EQISEM");
		if (!woEmSet.isEmpty() && woEmSet.count() > 0) {
			eqStatus = "EM";
		}
		
		eqStatusDesc = CommonUtil.getValue("L_ALNDOMAIN", "langcode='"+langCode+"' and ownerid in(select alndomainid from alndomain where domainid='UDEQSTATUS' and value='"+eqStatus+"')", "description");
		String assetTypeCode = mbo.getString("udassettypecode");
		if(assetTypeCode != null && "[SQ][SY][SR][SM][SOF][SE][LH][LB][GB][FB]".contains("["+ assetTypeCode + "]")){
			String udeqtsstatus = mbo.getString("udeqtsstatus");
			if(eqStatus.equalsIgnoreCase("INTACT") && udeqtsstatus != null && !udeqtsstatus.equalsIgnoreCase("")){
				eqStatusDesc = udeqtsstatus;
			}
		}
		
		this.getMboValue().setValue(eqStatusDesc, 11L);
	}
	
}
