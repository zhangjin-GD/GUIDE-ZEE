package guide.app.invmthly;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldInvmthlyCostSum extends MboValueAdapter {

	public FldInvmthlyCostSum(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote mbo = getMboValue().getMbo();
		MboSetRemote mboSet = mbo.getMboSet("UDINVMTHLYCOST");
		double curcostsum = mboSet.sum("curcost");
		double discostsum = mboSet.sum("discost");
		double disqtysum = mboSet.sum("disqty");
		double precostsum = mboSet.sum("precost");
		double reccostsum = mboSet.sum("reccost");
		double recrcostsum = mboSet.sum("recrcost");
		double sapdiscostsum = mboSet.sum("sapdiscost");
		double sapreccostsum = mboSet.sum("sapreccost");
		double sapusecostsum = mboSet.sum("sapusecost");
		double usecostsum = mboSet.sum("usecost");
		double usercostsum = mboSet.sum("usercost");

		mbo.setValue("curcostsum", curcostsum, 11L);
		mbo.setValue("discostsum", discostsum, 11L);
		mbo.setValue("disqtysum", disqtysum, 11L);
		mbo.setValue("precostsum", precostsum, 11L);
		mbo.setValue("reccostsum", reccostsum, 11L);
		mbo.setValue("recrcostsum", recrcostsum, 11L);
		mbo.setValue("sapdiscostsum", sapdiscostsum, 11L);
		mbo.setValue("sapreccostsum", sapreccostsum, 11L);
		mbo.setValue("sapusecostsum", sapusecostsum, 11L);
		mbo.setValue("usecostsum", usecostsum, 11L);
		mbo.setValue("usercostsum", usercostsum, 11L);
	}
}
