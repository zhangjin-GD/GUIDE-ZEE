package guide.webclient.beans.company;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelCompGradeToVendorDateBean extends DataBean {
	
	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.getParent().getMbo();
		if (owner != null) {
			MboSetRemote compGradeLineEn_BasicsSet = owner.getMboSet("UDCOMPGRADELINEEN_BASICS");
			MboSetRemote compGradeLineEn_ItSet = owner.getMboSet("UDCOMPGRADELINEEN_IT");
			MboSetRemote compGradeLineEnSet = owner.getMboSet("UDCOMPGRADELINEEN");
			MboRemote compGradeLineEn_Basics = null;
			MboRemote compGradeLineEn_It = null;
			MboRemote compGradeLineEn = null;
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				deleteLine(mr);
				if (!compGradeLineEn_BasicsSet.isEmpty() && compGradeLineEn_BasicsSet.count() > 0) {
					for (int j = 0; (compGradeLineEn_Basics = compGradeLineEn_BasicsSet.getMbo(j)) != null; j++) {
						compGradeLineEn = compGradeLineEnSet.add();
						setCompGradeLineEn(j, owner, mr, compGradeLineEn_Basics, compGradeLineEn);
					}
					mr.setValue("totalscore", owner.getDouble("totalscore"), 11L);
				}
				if (!compGradeLineEn_ItSet.isEmpty() && compGradeLineEn_ItSet.count() > 0) {
					for (int j = 0; (compGradeLineEn_It = compGradeLineEn_ItSet.getMbo(j)) != null; j++) {
						compGradeLineEn = compGradeLineEnSet.add();
						setCompGradeLineEn(j, owner, mr, compGradeLineEn_It, compGradeLineEn);
					}
					mr.setValue("totalscoreit", owner.getDouble("totalscoreit"), 11L);
				}
			}
		}
		return super.execute();
	}

	private void deleteLine(MboRemote mr) throws RemoteException, MXException {
		MboSetRemote compGradeLineEn_BasicsSet = mr.getMboSet("UDCOMPGRADELINEEN_BASICS");
		if (!compGradeLineEn_BasicsSet.isEmpty() && compGradeLineEn_BasicsSet.count() > 0) {
			compGradeLineEn_BasicsSet.deleteAll();
		}
		MboSetRemote compGradeLineEn_ItSet = mr.getMboSet("UDCOMPGRADELINEEN_IT");
		if (!compGradeLineEn_ItSet.isEmpty() && compGradeLineEn_ItSet.count() > 0) {
			compGradeLineEn_ItSet.deleteAll();
		}
	}

	private void setCompGradeLineEn(int i, MboRemote owner, MboRemote mr, MboRemote compGradeLineEn_X, MboRemote compGradeLineEn) throws RemoteException, MXException {
		compGradeLineEn.setValue("linenum", i+1, 11L);
		compGradeLineEn.setValue("cgnum", owner.getString("cgnum"), 11L);
		compGradeLineEn.setValue("vendor", mr.getString("vendor"), 11L);
		compGradeLineEn.setValue("normstype", compGradeLineEn_X.getString("normstype"), 11L);
		compGradeLineEn.setValue("indexvalue", compGradeLineEn_X.getString("indexvalue"), 11L);
		compGradeLineEn.setValue("indexscore", compGradeLineEn_X.getDouble("indexscore"), 11L);
		compGradeLineEn.setValue("score", compGradeLineEn_X.getDouble("score"), 2L);
	}
	
}
