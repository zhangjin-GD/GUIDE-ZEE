package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWOFailProblem extends MAXTableDomain {

	public FldWOFailProblem(MboValue mbv) {
		super(mbv);
		setRelationship("UDFAILTYPE", "failtypenum = :udfailproblem");
		String[] FromStr = { "failtypenum" };
		String[] ToStr = { "udfailproblem" };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public void validate() throws MXException, RemoteException {
//		super.validate();
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		Mbo mbo = this.getMboValue().getMbo();
		String udfailmech = mbo.getString("udfailmech");
		setListCriteria("type='P' and instr(failclassnum, substr('" + udfailmech + "',0,length(failclassnum)))>0 "
				+ " and length('" + udfailmech + "')>=length(failclassnum)");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		String thisAttr = getMboValue().getAttributeName();
		if ("UDFAILPROBLEM".equalsIgnoreCase(thisAttr)) {
			MboSetRemote failtypeSet = mbo.getMboSet("UDFAILTYPE_FAILPROBLEM");
			if (!failtypeSet.isEmpty() && failtypeSet.count() > 0) {
				MboRemote failtype = failtypeSet.getMbo(0);
				String desc = failtype.getString("description");
				mbo.setValue("udfailproblemdesc", desc, 11L);
			}
		}
	}
}
