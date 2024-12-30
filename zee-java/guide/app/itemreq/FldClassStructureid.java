package guide.app.itemreq;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldClassStructureid extends MboValueAdapter {

	public FldClassStructureid(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void validate() throws MXException, RemoteException {
		super.validate();
		Mbo mbo = getMboValue().getMbo();
		MboSetRemote classStrSet = mbo.getMboSet("CLASSSTRUCTURE");
		if (classStrSet != null && !classStrSet.isEmpty()) {
			MboSetRemote childrenSet = classStrSet.getMbo(0).getMboSet("CHILDREN");
			if (!childrenSet.isEmpty()) {
				throw new MXApplicationException("guide", "1190");
			}
		}
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		getMboValue().getMbo().setValueNull("itemnumauto", 11L);
		Mbo mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		String classstructureid = mbo.getString("classstructureid");
		MboSetRemote lineSet = mbo.getMboSet("CLASSSTRUCTURE");
		if (!lineSet.isEmpty()) {
			MboRemote line = lineSet.getMbo(0);
			String udmaterialtype = line.getString("udmaterialtype");
			mbo.setValue("materialtype", udmaterialtype, 11L);
		}
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			MboSetRemote classsCompanySet = mbo.getMboSet("$CLASSSCOMPANY", "CLASSSCOMPANY",
					"udcompany ='" + udcompany + "' and classstructureid ='" + classstructureid + "'");
			if (!classsCompanySet.isEmpty() && classsCompanySet.count() > 0) {
				MboRemote classsCompany = classsCompanySet.getMbo(0);
				String keeper = classsCompany.getString("keeper");
				String purchaser = classsCompany.getString("purchaser");
				mbo.setValue("keeper", keeper, 11L);
				mbo.setValue("purchaser", purchaser, 11L);
			}
		}
		if (this.getMboValue().isNull()) {
			mbo.setValueNull("materialtype", 11L);
		}
		// ZEE以1开头的类型设置默认库房必填，4开头的类型设置默认库房只读；
		String udcompany = mbo.getString("udcompany");
		if (!udcompany.isEmpty() && udcompany.equalsIgnoreCase("ZEE")) {
			String classificationid = mbo.getString("CLASSSTRUCTURE.classificationid");
			String classificationidfirst = classificationid.substring(0, 1);
			if (classificationidfirst.equalsIgnoreCase("1")) {
				mbo.setFieldFlag("storeloc", 128L, true);// 设置必填
				mbo.setFieldFlag("storeloc", 7L, false);// 取消设置只读
			}
			if (classificationidfirst.equalsIgnoreCase("4")) {
				mbo.setFieldFlag("storeloc", 128L, false);// 取消设置必填
				mbo.setFieldFlag("storeloc", 7L, true);// 设置只读
			}
		}
	}

}
