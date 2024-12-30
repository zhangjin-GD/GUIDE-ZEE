package guide.app.labor;

import java.rmi.RemoteException;

import psdi.app.labor.FldLaborPersonID;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldLaborPersonID extends FldLaborPersonID {

	public UDFldLaborPersonID(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		setListCriteria("personid not in (select personid from labor)");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		String personid = mbo.getString("personid");
		MboSetRemote personSet = mbo.getMboSet("PERSON");
		if (!personSet.isEmpty() && personSet.count() > 0) {
			MboRemote person = personSet.getMbo(0);
			String udcompany = person.getString("udcompany");
			mbo.setValue("laborcode", personid, 2L);
			mbo.setValue("udcompany", udcompany, 11L);
		}
	}
}
