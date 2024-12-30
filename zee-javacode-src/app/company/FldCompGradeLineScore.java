package guide.app.company;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldCompGradeLineScore extends MboValueAdapter {

	public FldCompGradeLineScore(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		int score = this.getMboValue().getInt();
		int indexscore = mbo.getInt("indexscore");
		if (score > indexscore || score < 0) {
			Object[] params = { indexscore };
			throw new MXApplicationException("guide", "1048", params);
		}
		if (parent != null && parent instanceof CompGrade) {
			parent.getMboSet("UDCOMPGRADELINE").resetQbe();
			double scoreSum = mbo.getThisMboSet().sum("score");
			parent.setValue("TOTALSCORE", scoreSum, 11L);
		}
	}
}
