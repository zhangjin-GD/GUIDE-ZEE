package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.FldInvUseLineAssetnum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldInvUseLineAssetnum extends FldInvUseLineAssetnum {

	public UDFldInvUseLineAssetnum(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String sql = "1=2";
		if (owner != null) {
			String movementType = owner.getString("udmovementtype");
			String company = owner.getString("udcompany");
			if (movementType != null && movementType.equalsIgnoreCase("204")) {
				sql = "udcompany='"+company+"' and udcostcenter is not null and udassettypecode='FAC' and udassettypecode1='BLDG'";
			} else if (movementType != null && (movementType.equalsIgnoreCase("205") || movementType.equalsIgnoreCase("207"))) {
				sql = "udcompany='"+company+"' and udcostcenter is not null and udassettypecode!='FAC'";
			} else if (movementType != null && movementType.equalsIgnoreCase("206")) {
				sql = "udcompany='"+company+"' and udcostcenter is not null and udassettypecode='FAC' and nvl(udassettypecode1,'NA') not in ('BLDG','CNTN') ";
			} else if (movementType != null && movementType.equalsIgnoreCase("221")) {
				sql = "udcompany='"+company+"' and udcostcenter is not null and udassettypecode='FAC' and udassettypecode1='CNTN'";
			}
		}
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void validate() throws RemoteException, MXException {
		super.validate();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote assetSet = mbo.getMboSet("ASSET");
		if (!assetSet.isEmpty() && assetSet.count() > 0) {
			MboRemote asset = assetSet.getMbo(0);
			if (asset.isNull("udcostcenter")) {
				throw new MXApplicationException("guide", "1018");
			}
			MboRemote owner = mbo.getOwner();
			if (owner != null) {
				String movementType = owner.getString("udmovementtype");
				String company = owner.getString("udcompany");
				String assetTypeCode = asset.getString("udassettypecode");
				String assetTypeCode1 = asset.getString("udassettypecode1");
				String assetCompany = asset.getString("udcompany");
				if(!company.equalsIgnoreCase(assetCompany)){
					throw new MXApplicationException("guide", "1043");
				}
				if ((movementType.equalsIgnoreCase("205") || movementType.equalsIgnoreCase("207")) && assetTypeCode.equalsIgnoreCase("FAC")) {
					throw new MXApplicationException("guide", "1043");
				} else if (movementType.equalsIgnoreCase("204") && !assetTypeCode1.equalsIgnoreCase("BLDG")) {
					throw new MXApplicationException("guide", "1043");
				} else if (movementType.equalsIgnoreCase("206")
						&& (assetTypeCode1.equalsIgnoreCase("BLDG") || assetTypeCode1.equalsIgnoreCase("CNTN"))) {
					throw new MXApplicationException("guide", "1043");
				} else if (movementType.equalsIgnoreCase("221") && !assetTypeCode1.equalsIgnoreCase("CNTN")) {
					throw new MXApplicationException("guide", "1043");
				}
			}
		}
	}

}
