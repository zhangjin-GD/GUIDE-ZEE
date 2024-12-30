package guide.app.company;

import java.rmi.RemoteException;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class CpReviewLine extends Mbo implements MboRemote {

	public CpReviewLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if ((parent != null) && (parent instanceof CpReview)) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			String cpreviewnum = parent.getString("cpreviewnum");
			this.setValue("linenum", linenum, 11L);
			this.setValue("cpreviewnum", cpreviewnum, 11L);
		}
	}
}
