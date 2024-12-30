package guide.app.pr;

import java.rmi.RemoteException;

import guide.app.common.FldProjectNum;
import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldPrProjectNum extends MAXTableDomain {

	public UDFldPrProjectNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDPROJECT", "PROJECTNUM=:" + thisAttr);
		String[] FromStr = { "PROJECTNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String udprojectnum = mbo.getString("udprojectnum");
		String udcapex = mbo.getString("udcapex");
		MboSetRemote lineSet = mbo.getMboSet("PRLINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			for (int i = 0; i < lineSet.count(); i++) {
				MboRemote line = lineSet.getMbo(i);
				line.setValue("udprojectnum", udprojectnum, 11L);
				line.setValue("udcapex", udcapex, 11L);
			}
		}
	}
	
	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		String udcompany = mbo.getString("udcompany");
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			setListCriteria("status = 'APPR'");
		}
		return super.getList();
	}
}