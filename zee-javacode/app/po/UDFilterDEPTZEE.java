package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
/**
 * 用于过滤ZEE部门
 * @author djy
 *
 */
public class UDFilterDEPTZEE extends MAXTableDomain{
	public UDFilterDEPTZEE(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDDEPT", "deptnum=:" + thisAttr);
		String[] FromStr = { "deptnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote list = super.getList();
				list.setWhere(" type = 'DEPARTMENT' and parent='ZEE' ");
				list.reset();
		return list;
	}
}
