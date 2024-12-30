package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPrLineFaClassNum extends MAXTableDomain {

	public FldPrLineFaClassNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDFIXEDASSETCL", "faclassnum = :" + thisAttr);
		String[] FromStr = { "faclassnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		Mbo mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String sql = "1=1";
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			MboSetRemote clCpConSet = mbo.getMboSet("UDFIXEDASSETCLCPCON");
			if (!clCpConSet.isEmpty() && clCpConSet.count() > 0) {
				sql += " and exists (select 1 from udfixedassetclcp where udfixedassetclcp.faclassnum=udfixedassetcl.faclassnum"
						+ " and udfixedassetclcp.udcompany='" + udcompany + "')";
			}
		}
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote lineSet = mbo.getMboSet("UDFIXEDASSETCLCP");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			MboRemote line = lineSet.getMbo(0);
			mbo.setValue("udfixdept", line.getString("uddept"), 11L);
			mbo.setValue("udfixassetadmin", line.getString("fixassetadmin"), 11L);
		}
		if (this.getMboValue().isNull()) {
			mbo.setValueNull("udfixdept", 11L);
			mbo.setValueNull("udfixassetadmin", 11L);
		}
	}
}
