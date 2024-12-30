package guide.iface.sap;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldSapHeaderCompany extends MAXTableDomain {

	public FldSapHeaderCompany(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDDEPT", "deptnum = :" + thisAttr);
		String[] FromStr = { "deptnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("type = 'COMPANY'");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote companySet = mbo.getMboSet("UDCOMPANY");
		if (!companySet.isEmpty() && companySet.count() > 0) {
			MboRemote company = companySet.getMbo(0);
			String costcenter = company.getString("costcenter");// 公司代码
			String sapzsource = company.getString("sapzsource");// SAP原系统
			mbo.setValue("bukrs", costcenter, 11L);
			mbo.setValue("zsource", sapzsource, 11L);
		}
	}
}
