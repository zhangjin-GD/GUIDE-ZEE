package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
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
		//ZEE，则使用 UDPROJECTNUM 作为关系键；否则，使用 PROJECTNUM 作为关系键
		MboRemote mbo = getMboValue().getMbo();
		try {
			String udcompany = mbo.getString("udcompany");
	        String relationship = "UDPROJECT";
	        String fromAttr = "PROJECTNUM";
	        if ("ZEE".equalsIgnoreCase(udcompany)) {
	            relationship = "UDPROJECT";
	            fromAttr = "UDPROJECTNUM";
	        }
			setRelationship(relationship, fromAttr + " =:" + thisAttr);
	        String[] FromStr = { fromAttr };
	        String[] ToStr = { thisAttr };
	        setLookupKeyMapInOrder(ToStr, FromStr);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote mboSet = super.getList();
		MboRemote mbo = getMboValue().getMbo();
		//ZEE关系与其他码头不同
		if(mbo!=null && mbo instanceof UDWO && mbo.getString("udcompany").equalsIgnoreCase("ZEE")){
			mboSet.setUserWhere("status = 'APPR' and udcompany = 'ZEE' ");
		}else{
			mboSet.setUserWhere("status = 'APPR'");
		}
		return mboSet;
	}
}
