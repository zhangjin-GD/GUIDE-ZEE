package guide.app.item;

import java.rmi.RemoteException;

import guide.app.common.CommonUtil;
import psdi.app.item.Item;
import psdi.app.item.ItemRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDItem extends Item implements ItemRemote {

	public UDItem(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			// 登录人
			String personId = this.getUserInfo().getPersonId();
			String[] attrs = { "DESCRIPTION", "ORDERUNIT", "ISSUEUNIT", "UDMATERIALTYPE", "LOTTYPE", "UDLONGDESC",
					"UDMAXLIMIT", "UDMINLIMIT", "UDMODELNUM", "UDSPECS", "UDTECHPARAM", "UDMANUFACTURER", "UDPARTNUM",
					"CREATEDBYTGZ", "GREEKDESCRIPTION", "UDISGROUPPUR", "UDISTECHACCE", "UDISSAFETY", "UDISFIX",
					"UDISIMPORT", "UDISSHARE", "UDISCONSTR", "UDISINSPECT", "TEMPCODE", "UDLOCKSTD", "UDTEUSTD",
					"UDUNITSTD", "UDACTIONSTD", "UDRUNSTD", "UDCALSTD" };
			// 是否管理员组
			if (!CommonUtil.isAdmin(personId)) {
				this.setFieldFlag(attrs, READONLY, true);
			} else {
				this.setFieldFlag(attrs, READONLY, false);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() throws MXException, RemoteException {
		super.save();

		if (this.isModified("description") || this.isModified("udmodelnum") || this.isModified("udspecs")) {
			String udlongdesc = "";
			if (!this.isNull("description")) {
				String desc = this.getString("description");
				udlongdesc += desc + "//";
			}
			if (!this.isNull("udmodelnum")) {
				String modelnum = this.getString("udmodelnum");
				udlongdesc += modelnum + "//";
			}
			if (!this.isNull("udspecs")) {
				String specs = this.getString("udspecs");
				udlongdesc += specs + "//";
			}
			if (udlongdesc.length() > 0) {
				udlongdesc = udlongdesc.substring(0, udlongdesc.length() - 2);
			}
			this.setValue("udlongdesc", udlongdesc, 11L);
		}

		// try {
		// String status=BarCodeUtils.BarCode(getString("itemnum"),
		// "D:\\DOCLINKS\\BARCode\\ITEM\\", getString("itemnum"));
		// System.out.println("\n-------------------BarCode:"+status);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

}
