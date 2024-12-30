package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldGjpNum extends MAXTableDomain {

	public FldGjpNum(MboValue mbv) throws MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDGJOBPLAN", "gjpnum=:" + thisAttr);
		String[] FromStr = { "gjpnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		Mbo mbo = getMboValue().getMbo();
		String sql = "status='ACTIVE' and udcompany=:udcompany and uddept=:uddept";
		if (!mbo.isNull("udofs")) {
			sql = sql + " and udofs=:udofs";
		}
		sql = sql + " and assettype=(select udassettypecode from asset where assetnum=:assetnum and status='ACTIVE')";
		/**
		 * ZEE
		 * 2023-07-20 16:06:08
		 */
		String udcompany = mbo.getString("udcompany");
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			sql = " status='ACTIVE' and udcompany='"+udcompany+"' ";
		}
		setListCriteria(sql);
		return super.getList();
	}

}
