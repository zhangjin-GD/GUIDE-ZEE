package guide.app.matctrl;


import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatCtrlUseAct extends MboValueAdapter {

	public FldMatCtrlUseAct(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		
		MboRemote mbo = this.getMboValue().getMbo();
		double cpuseact = 0.0d;
		double deptuseact = 0.0d;
		double ofsuseact = 0.0d;
		
		String cpUseActSql = CommonUtil.getValue("MAXRELATIONSHIP", "parent='UDMATCTRL' and name='CPUSEACT'", "whereclause");
		if (cpUseActSql != null && !cpUseActSql.equalsIgnoreCase("")) {
			cpUseActSql = cpUseActSql.replace(":udcompany", "'" + mbo.getString("udcompany") + "'");
			cpuseact = CommonUtil.getSumValue("INVUSELINE", cpUseActSql, "linecost");
		}
		
		String deptUseActSql = CommonUtil.getValue("MAXRELATIONSHIP", "parent='UDMATCTRL' and name='DEPTUSEACT'", "whereclause");
		if (deptUseActSql != null && !deptUseActSql.equalsIgnoreCase("")) {
			deptUseActSql = deptUseActSql.replace(":udcompany", "'" + mbo.getString("udcompany") + "'");
			deptUseActSql = deptUseActSql.replace(":uddept", "'" + mbo.getString("uddept") + "'");
			deptuseact = CommonUtil.getSumValue("INVUSELINE", deptUseActSql, "linecost");
		}
		
		String ofsUseActSql = CommonUtil.getValue("MAXRELATIONSHIP", "parent='UDMATCTRL' and name='OFSUSEACT'", "whereclause");
		if (ofsUseActSql != null && !ofsUseActSql.equalsIgnoreCase("")) {
			ofsUseActSql = ofsUseActSql.replace(":udcompany", "'" + mbo.getString("udcompany") + "'");
			ofsUseActSql = ofsUseActSql.replace(":uddept", "'" + mbo.getString("uddept") + "'");
			ofsUseActSql = ofsUseActSql.replace(":udofs", "'" + mbo.getString("udofs") + "'");
			ofsuseact = CommonUtil.getSumValue("INVUSELINE", ofsUseActSql, "linecost");
		} 
		
		mbo.setValue("cpuseact", cpuseact, 11L);
		mbo.setValue("deptuseact", deptuseact, 11L);
		mbo.setValue("ofsuseact", ofsuseact, 11L);
	}
	
}
