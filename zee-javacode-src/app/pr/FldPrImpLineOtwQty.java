package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPrImpLineOtwQty extends MboValueAdapter {

	public FldPrImpLineOtwQty(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();

		MboRemote mbo = this.getMboValue().getMbo();
		double matpr = 0.0d;
		double matpo = 0.0d;
		String prRelation = "UDMATPRLINEOTW";
		String poRelation = "UDMATPOLINEOTW";

		MboSetRemote matinprSet = mbo.getMboSet(prRelation);
		if (matinprSet != null && !matinprSet.isEmpty()) {
			matpr = matinprSet.sum("orderqty");
		}

		MboSetRemote matinpoSet = mbo.getMboSet(poRelation);
		if (matinpoSet != null && !matinpoSet.isEmpty()) {
			matpo = matinpoSet.sum("orderqty");
		}

		mbo.setValue("otwqty", matpr + matpo, 11L);
		mbo.setValue("udotwqtyiss", (matpr + matpo) * mbo.getDouble("conversion"), 11L);
	}
}
