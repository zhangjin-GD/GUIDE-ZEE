package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldAssetStopdur extends MboValueAdapter {

	public FldAssetStopdur(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double failDur = 0.00d;
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote faildurSet = mbo.getMboSet("UDFAILDUR");
		if (!faildurSet.isEmpty() && faildurSet.count() > 0) {
			failDur = faildurSet.sum("udfaildur");
		}
		double stopDur = 0.00d;
		MboSetRemote stopdurSet = mbo.getMboSet("UDSTOPDUR");
		if (!stopdurSet.isEmpty() && stopdurSet.count() > 0) {
			stopDur = stopdurSet.sum("estdur");
		}
		this.getMboValue().setValue(failDur+stopDur, 11L);
	}
	
}
