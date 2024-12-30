package guide.app.company;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldCompGradeNormsType extends MboValueAdapter {

	public FldCompGradeNormsType(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		String cgnum = mbo.getString("cgnum");
		MboSetRemote originalSet = mbo.getMboSet("UDCOMPNORMS");
		if (originalSet != null && !originalSet.isEmpty()) {
			MboSetRemote newLineSet = mbo.getMboSet("UDCOMPGRADELINE");
			newLineSet.setOrderBy("linenum");
			if (newLineSet != null && !newLineSet.isEmpty()) {
				newLineSet.deleteAll(11L);
			}
			for (int i = 0; originalSet.getMbo(i) != null; i++) {
				MboRemote newLine = newLineSet.addAtEnd();
				MboRemote original = originalSet.getMbo(i);
				int linenum = original.getInt("linenum");
				String index1 = original.getString("index1");
				String index2 = original.getString("index2");
				String indexvalue = original.getString("indexvalue");
				int indexscore = original.getInt("indexscore");

				newLine.setValue("cgnum", cgnum, 11L);
				newLine.setValue("linenum", linenum, 11L);
				newLine.setValue("index1", index1, 11L);
				newLine.setValue("index2", index2, 11L);
				newLine.setValue("indexvalue", indexvalue, 11L);
				newLine.setValue("indexscore", indexscore, 11L);
				newLine.setValue("score", indexscore, 2L);
			}
		}
	}
}
