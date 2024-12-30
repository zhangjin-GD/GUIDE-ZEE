package guide.app.company;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldCompGradeLineScoreEN extends MboValueAdapter {

	public FldCompGradeLineScoreEN(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		double score = this.getMboValue().getDouble();
		if (score > 100 || score < 0) {
			Object[] params = { 100 };
			throw new MXApplicationException("guide", "1048", params);
		}
		if (parent != null) {
//		if (parent != null && parent instanceof CompGrade) {
			double indexscore = mbo.getDouble("indexscore");
			double result = (indexscore * score) / 100;
			mbo.setValue("result", result, 11L);
			MboSetRemote thisMboSet = mbo.getThisMboSet();
			String relationName = thisMboSet.getRelationName();
			parent.getMboSet(relationName).resetQbe();
			if ("UDCOMPGRADELINEEN_BASICS".equalsIgnoreCase(relationName)) {
				double resultSum = mbo.getThisMboSet().sum("result");
				parent.setValue("TOTALSCORE", resultSum, 11L);
			}
			if ("UDCOMPGRADELINEEN_IT".equalsIgnoreCase(relationName)) {
				double resultSum = mbo.getThisMboSet().sum("result");
				parent.setValue("TOTALSCOREIT", resultSum, 11L);
			}
		}
	}
}
