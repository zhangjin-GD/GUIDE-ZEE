package guide.app.common;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboValue;

public class FldComCurrencyCode extends MAXTableDomain {

	public FldComCurrencyCode(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDCURRENCY", "currencycode =:" + thisAttr);
		String[] FromStr = { "currencycode" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
}
