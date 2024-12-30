package guide.app.company;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldCompGradeNormsTypeEN extends MboValueAdapter {

	public FldCompGradeNormsTypeEN(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();

		String cgnum = mbo.getString("cgnum");
		String normstypeen = mbo.getString("normstypeen");
		if ("IT".equalsIgnoreCase(normstypeen)) {
			MboSetRemote basicslineSet = mbo.getMboSet("UDCOMPNORMS_BASICS");
			if (!basicslineSet.isEmpty() && basicslineSet.count() > 0) {
				MboSetRemote newLineSet = mbo.getMboSet("UDCOMPGRADELINEEN_BASICS");
				newLineSet.setOrderBy("linenum");
				if (newLineSet.isEmpty()) {
					for (int i = 0; basicslineSet.getMbo(i) != null; i++) {
						MboRemote newLine = newLineSet.addAtEnd();
						MboRemote original = basicslineSet.getMbo(i);
						int linenum = original.getInt("linenum");
						String normstype = original.getString("normstype");
						String indexvalue = original.getString("indexvalue");
						int indexscore = original.getInt("indexscore");

						newLine.setValue("cgnum", cgnum, 11L);
						newLine.setValue("linenum", linenum, 11L);
						newLine.setValue("normstype", normstype, 11L);
						newLine.setValue("indexvalue", indexvalue, 11L);
						newLine.setValue("indexscore", indexscore, 11L);
						newLine.setValue("score", 0, 2L);
					}
				}
			}
			MboSetRemote itlineSet = mbo.getMboSet("UDCOMPNORMS_IT");
			if (!itlineSet.isEmpty() && itlineSet.count() > 0) {
				MboSetRemote newLineSet = mbo.getMboSet("UDCOMPGRADELINEEN_IT");
				newLineSet.setOrderBy("linenum");
				if (newLineSet.isEmpty()) {
					for (int i = 0; itlineSet.getMbo(i) != null; i++) {
						MboRemote newLine = newLineSet.addAtEnd();
						MboRemote original = itlineSet.getMbo(i);
						int linenum = original.getInt("linenum");
						String normstype = original.getString("normstype");
						String indexvalue = original.getString("indexvalue");
						int indexscore = original.getInt("indexscore");

						newLine.setValue("cgnum", cgnum, 11L);
						newLine.setValue("linenum", linenum, 11L);
						newLine.setValue("normstype", normstype, 11L);
						newLine.setValue("indexvalue", indexvalue, 11L);
						newLine.setValue("indexscore", indexscore, 11L);
						newLine.setValue("score", 0, 2L);
					}
				}
			}
		}
		if (!"IT".equalsIgnoreCase(normstypeen)) {
			MboSetRemote lineSet = mbo.getMboSet("UDCOMPNORMS_BASICS");
			if (!lineSet.isEmpty() && lineSet.count() > 0) {
				MboSetRemote newLineSet = mbo.getMboSet("UDCOMPGRADELINEEN_BASICS");
				newLineSet.setOrderBy("linenum");
				if (newLineSet.isEmpty()) {
					for (int i = 0; lineSet.getMbo(i) != null; i++) {
						MboRemote newLine = newLineSet.addAtEnd();
						MboRemote original = lineSet.getMbo(i);
						int linenum = original.getInt("linenum");
						String normstype = original.getString("normstype");
						String indexvalue = original.getString("indexvalue");
						int indexscore = original.getInt("indexscore");

						newLine.setValue("cgnum", cgnum, 11L);
						newLine.setValue("linenum", linenum, 11L);
						newLine.setValue("normstype", normstype, 11L);
						newLine.setValue("indexvalue", indexvalue, 11L);
						newLine.setValue("indexscore", indexscore, 11L);
						newLine.setValue("score", 0, 2L);
					}
				}
			}
			MboSetRemote newLineSet = mbo.getMboSet("UDCOMPGRADELINEEN_IT");
			if (!newLineSet.isEmpty() && newLineSet.count() > 0) {
				newLineSet.deleteAll(11L);
			}
		}
	}
}
