package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.app.workorder.FldWOAssetnum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldWOAssetnum extends FldWOAssetnum {

	public UDFldWOAssetnum(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void init() throws MXException, RemoteException {
		super.init();

		MboRemote mbo = getMboValue().getMbo();
		String assetnum = mbo.getString("assetnum");
		if (assetnum != null && !assetnum.equalsIgnoreCase("")) {
			mbo.setFieldFlag("udassettypecode", 7L, true);
		} else {
			mbo.setFieldFlag("udassettypecode", 7L, false);
		}
	}

	@Override
	public void validate() throws MXException, RemoteException {
		super.validate();
		MboRemote mbo = getMboValue().getMbo();
		String worktype = mbo.getString("worktype");
		if ("EM".equalsIgnoreCase(worktype)) {
			MboSetRemote woSet = mbo.getMboSet("EQISEM");
			if (woSet.count() > 0) {
				throw new MXApplicationException("guide", "1056");
			}
		}
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String assetTypeCode = mbo.getString("udassettypecode");
		String sql = "status in('ACTIVE','OPERATING')";
		if (!mbo.isNull("udassettypecode")) {
			sql += " and udassettypecode = '" + assetTypeCode + "'";
		}
		
		/**
		 * ZEE-工单选择设备时不根据assettype进行过滤
		 * 2023-07-28 11:37:03
		 */
		String udcompany = mbo.getString("udcompany");
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			sql = " 1=1 and udcompany='"+udcompany+"' ";
		}
		
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String worktype = mbo.getString("worktype");
		String description = mbo.getString("description");
		MboSetRemote assetSet = mbo.getMboSet("ASSET");
		if (!assetSet.isEmpty() && assetSet.count() > 0) {
			MboRemote asset = assetSet.getMbo(0);
			if (!"SW".equalsIgnoreCase(worktype)) {
				mbo.setValue("udcompany", asset.getString("udcompany"), 11L);
				mbo.setValue("uddept", asset.getString("uddept"), 11L);
				mbo.setValue("udofs", asset.getString("udofs"), 11L);
				mbo.setValue("udcrew", asset.getString("udcrew"), 11L);
			}
			mbo.setValue("udfailasset", asset.getString("udfailclassnum"), 2L);

			if (!mbo.getUserInfo().getLangCode().equalsIgnoreCase("EN")) {
				if (worktype != null && worktype.equalsIgnoreCase("EM")
						&& (description == null || description.equalsIgnoreCase(""))) {
					mbo.setValue("description", asset.getString("description") + "故障", 11L);
				} else if (worktype != null && worktype.equalsIgnoreCase("CM")
						&& (description == null || description.equalsIgnoreCase(""))) {
					mbo.setValue("description", asset.getString("description") + "维修", 11L);
				}
			}

			MboSetRemote persongroupSet = mbo.getMboSet("FINDPERSONGROUP");
			if (!persongroupSet.isEmpty() && persongroupSet.count() > 0) {
				mbo.setValue("persongroup", persongroupSet.getMbo(0).getString("persongroup"), 11L);
			}
			if (mbo.getString("udassettypecode") == null || mbo.getString("udassettypecode").equalsIgnoreCase(""))
				mbo.setValue("udassettypecode", asset.getString("udassettypecode"), 11L);
		}
		if (getMboValue().isNull()) {
			if (!"SW".equalsIgnoreCase(worktype)) {
				mbo.setValueNull("udcompany", 11L);
				mbo.setValueNull("uddept", 11L);
				mbo.setValueNull("udofs", 11L);
				mbo.setValueNull("udcrew", 11L);
			}
			mbo.setValueNull("udfailasset", 11L);
			if (!mbo.getUserInfo().getLangCode().equalsIgnoreCase("EN")) {
				if (worktype != null && (worktype.equalsIgnoreCase("EM") || worktype.equalsIgnoreCase("CM"))) {
					mbo.setValueNull("description", 11L);
				}
			}
			mbo.setValueNull("persongroup", 11L);
			mbo.setFieldFlag("udassettypecode", 7L, false);
		} else {
			mbo.setFieldFlag("udassettypecode", 7L, true);
		}
	}

}
