package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldPOLinePOLineId extends MAXTableDomain {

	public UDFldPOLinePOLineId(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("POLINE", "polineid=:" + thisAttr);
		String[] FromStr = { "polineid" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String sql = "receiptscomplete =1 and udpolineid is null and exists (select 1 from prline where prline.polineid=poline.polineid and udseritemnum is not null)";
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			sql += "and exists(select 1 from po where ponum=poline.ponum and status not in ('CAN') and udcompany='"
					+ udcompany + "')";
		}
		setListCriteria(sql);
		return super.getList();
	}
}
