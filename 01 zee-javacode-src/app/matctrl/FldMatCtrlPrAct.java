package guide.app.matctrl;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatCtrlPrAct extends MboValueAdapter {

	public FldMatCtrlPrAct(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		
		MboRemote mbo = this.getMboValue().getMbo();
		double cppract = 0.0d;
		double deptpract = 0.0d;
		double ofspract = 0.0d;
		
		MboSetRemote cpPractPrlineSet = mbo.getMboSet("CPPRACTPRLINE");
		if (!cpPractPrlineSet.isEmpty() && cpPractPrlineSet.count() > 0) {
			cppract =+ cpPractPrlineSet.sum("linecost") + cpPractPrlineSet.sum("tax1");
		}
		MboSetRemote cpPractPolineSet = mbo.getMboSet("CPPRACTPOLINE");
		if (!cpPractPolineSet.isEmpty() && cpPractPolineSet.count() > 0) {
			cppract =+ cpPractPolineSet.sum("linecost") + cpPractPolineSet.sum("tax1");
		}
		
		MboSetRemote deptPractPrlineSet = mbo.getMboSet("DEPTPRACTPRLINE");
		if (!deptPractPrlineSet.isEmpty() && deptPractPrlineSet.count() > 0) {
			deptpract =+ deptPractPrlineSet.sum("linecost") + deptPractPrlineSet.sum("tax1");
		}
		MboSetRemote deptPractPolineSet = mbo.getMboSet("DEPTPRACTPOLINE");
		if (!deptPractPolineSet.isEmpty() && deptPractPolineSet.count() > 0) {
			deptpract =+ deptPractPolineSet.sum("linecost") + deptPractPolineSet.sum("tax1");
		}
		
		MboSetRemote ofsPractPrlineSet = mbo.getMboSet("OFSPRACTPRLINE");
		if (!ofsPractPrlineSet.isEmpty() && ofsPractPrlineSet.count() > 0) {
			ofspract =+ ofsPractPrlineSet.sum("linecost") + ofsPractPrlineSet.sum("tax1");
		}
		MboSetRemote ofsPractPolineSet = mbo.getMboSet("OFSPRACTPOLINE");
		if (!ofsPractPolineSet.isEmpty() && ofsPractPolineSet.count() > 0) {
			ofspract =+ ofsPractPolineSet.sum("linecost") + ofsPractPolineSet.sum("tax1");
		}
		
		mbo.setValue("cppract", cppract, 11L);
		mbo.setValue("deptpract", deptpract, 11L);
		mbo.setValue("ofspract", ofspract, 11L);
	}
	
}
