package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/**
 * 选择项目管理
 * @author Administrator
 * @person sxd
 * @date 2021年11月30日
 */
public class FldProject extends MAXTableDomain{

	public FldProject(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDPROJECT", "PROJECTNUM =:" + thisAttr);
		String[] FromStr = { "PROJECTNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote mboSet = super.getList();
		mboSet.setUserWhere("status = 'APPR'");
		return mboSet;
	}
}
