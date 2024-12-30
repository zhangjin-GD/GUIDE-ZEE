package guide.app.woremain;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboValue;

/**
 * 选择工作负责人
 * @author Administrator
 *sxd
 */
public class FldWorkLeader extends MAXTableDomain{

	public FldWorkLeader(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("PERSON", "PERSONID =:" + thisAttr);
		String[] FromStr = { "PERSONID" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
}
