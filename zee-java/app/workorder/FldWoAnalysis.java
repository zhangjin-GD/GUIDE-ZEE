package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWoAnalysis extends MboValueAdapter {

	public FldWoAnalysis(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void init() throws MXException, RemoteException {
		super.init();
		MboRemote mbo = this.getMboValue().getMbo();
		String woAnalysis = mbo.getString("udwoanalysis");
		String[] attrs = { "actstart", "actfinish", "udfailmech", "udfailtype", "udfailproblemdesc", "udfailcausedesc",	"udfailremedydesc" };
		if (woAnalysis != null && (woAnalysis.equalsIgnoreCase("1FAULT") || woAnalysis.equalsIgnoreCase("5ACCIDENT"))) {
			mbo.setFieldFlag(attrs, 128L, true);
		} else {
			mbo.setFieldFlag(attrs, 128L, false);
		}
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String woAnalysis = mbo.getString("udwoanalysis");
		String[] attrs = { "actstart", "actfinish", "udfailmech", "udfailtype", "udfailproblemdesc", "udfailcausedesc", "udfailremedydesc" };
		if (woAnalysis != null && (woAnalysis.equalsIgnoreCase("1FAULT") || woAnalysis.equalsIgnoreCase("5ACCIDENT"))) {
			mbo.setFieldFlag(attrs, 128L, true);
		} else {
			mbo.setFieldFlag(attrs, 128L, false);
		}
	}

}
