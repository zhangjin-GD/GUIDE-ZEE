package guide.app.company;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldUseDepartment extends MAXTableDomain {

	public FldUseDepartment(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDDEPT", "deptnum = :" + thisAttr);
		String[] FromStr = { "deptnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "type='DEPARTMENT' and parent=:udcompany";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote signVendorSet = mbo.getMboSet("SIGNVENDOR");
		MboRemote signVendor = null;
		MboSetRemote compGradeVendorSet = mbo.getMboSet("UDCOMPGRADEVENDOR");
		if (!compGradeVendorSet.isEmpty() && compGradeVendorSet.count() > 0) {
			compGradeVendorSet.deleteAll();
		}
		
		String useDept = mbo.getString("usedept");
		if(useDept != null && !useDept.equalsIgnoreCase("")){
			MboRemote compGradeVendor = null;
			for (int i = 0; (signVendor = signVendorSet.getMbo(i)) != null; i++) {
				compGradeVendor = compGradeVendorSet.add();
				compGradeVendor.setValue("linenum", i+1, 11L);
				compGradeVendor.setValue("cgnum", mbo.getString("cgnum"), 11L);
				compGradeVendor.setValue("vendor", signVendor.getString("company"), 11L);
			}
		}
		
	}
	
}
