package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldProjectNum extends MAXTableDomain {

	public FldProjectNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDPROJECT", "PROJECTNUM=:" + thisAttr);
		String[] FromStr = { "PROJECTNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("status = 'APPR'");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote projectSet = mbo.getMboSet("UDPROJECT");
		if (!projectSet.isEmpty()) {
			MboRemote project = projectSet.getMbo(0);
			String budgetnum = project.getString("budgetnum");
			mbo.setValue("budgetnum", budgetnum, 11L);
		}
		if (this.getMboValue().isNull()) {
			mbo.setValueNull("budgetnum", 11L);
		}
	}
}
