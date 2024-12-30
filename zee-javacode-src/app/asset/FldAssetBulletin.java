package guide.app.asset;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldAssetBulletin extends MboValueAdapter {

	public FldAssetBulletin(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		
		int assetqtyt = 0;
		int assetqtys = 0;
		int assetqtye = 0;
		int assetqtyc = 0;
		int assetqtyp = 0;
		int assetqtyi = 0;
		
		MboRemote mbo = this.getMboValue().getMbo();
		
		MboSetRemote assetqtytSet = mbo.getMboSet("ASSETQTYT");
		if (!assetqtytSet.isEmpty() && assetqtytSet.count() > 0) {
			assetqtyt = assetqtytSet.count();
		}
		
		MboSetRemote assetqtysSet = mbo.getMboSet("ASSETQTYS");
		if (!assetqtysSet.isEmpty() && assetqtysSet.count() > 0) {
			assetqtys = assetqtysSet.count();
		}
		
		MboSetRemote assetqtyeSet = mbo.getMboSet("ASSETQTYE");
		if (!assetqtyeSet.isEmpty() && assetqtyeSet.count() > 0) {
			assetqtye = assetqtyeSet.count();
		}
		
		MboSetRemote assetqtycSet = mbo.getMboSet("ASSETQTYC");
		if (!assetqtycSet.isEmpty() && assetqtycSet.count() > 0) {
			assetqtyc = assetqtycSet.count();
		}
		
		MboSetRemote assetqtypSet = mbo.getMboSet("ASSETQTYP");
		if (!assetqtypSet.isEmpty() && assetqtypSet.count() > 0) {
			assetqtyp = assetqtypSet.count();
		}
		
		MboSetRemote assetqtyiSet = mbo.getMboSet("ASSETQTYI");
		if (!assetqtyiSet.isEmpty() && assetqtyiSet.count() > 0) {
			assetqtyi = assetqtyiSet.count();
		}
		
		mbo.setValue("assetqtyt", assetqtyt, 11L);
		mbo.setValue("assetqtys", assetqtys, 11L);
		mbo.setValue("assetqtye", assetqtye, 11L);
		mbo.setValue("assetqtyc", assetqtyc, 11L);
		mbo.setValue("assetqtyp", assetqtyp, 11L);
		mbo.setValue("assetqtyi", assetqtyi, 11L);
		mbo.setValue("assetqtyr", assetqtyt-assetqtys, 11L);
	}
	
}
