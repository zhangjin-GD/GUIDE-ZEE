package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldCompanyAssetNumLine extends MAXTableDomain {

	public FldCompanyAssetNumLine(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ASSET", "assetnum =:" + thisAttr);
		String[] FromStr = { "assetnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "status in ('ACTIVE','OPERATING')";
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if(owner != null){
			String company = owner.getString("udcompany");
			if(company != null && !company.equalsIgnoreCase("")){
				sql += " and udcompany='"+company+"'";
			}
		}
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}

}
