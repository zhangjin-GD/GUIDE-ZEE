package guide.app.project;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldPropayTotallinetaxcost extends MboValueAdapter {

	private static final char[] CN_UPPER_NUMBER = "零壹贰叁肆伍陆柒捌玖".toCharArray();

	private static final char[] CN_UPPER_UNIT = "仟佰拾".toCharArray();

	private static final char[] CN_GROUP = "元万亿".toCharArray();

	public FldPropayTotallinetaxcost(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double totallinetaxcost = this.getMboValue().getDouble();
		double linecost = mbo.getDouble("linecost");
		if (totallinetaxcost > linecost) {
			Object[] obj = { "温馨提示：已超出需要支付的总金额！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}
		double residualcost = linecost - totallinetaxcost;
		String amountinwords = moneyToChinese(new BigDecimal(totallinetaxcost));
		mbo.setValue("amountinwords", amountinwords, 11L);
		mbo.setValue("residualcost", residualcost, 11L);
	}

	private String moneyToChinese(BigDecimal money) {
		if (money.equals(BigDecimal.ZERO)) {
			return "零元整";
		}
		Double max = 1000000000000D;
		Double min = 0.01D;
		if (money.doubleValue() >= max || money.doubleValue() < min) {
			return "大于1万亿或小于1分";
		}
		money = money.setScale(2, RoundingMode.HALF_UP);
		String numStr = money.toString();
		int pointPos = numStr.indexOf('.');
		// 整数部分
		String s_int = null;
		// 小数部分
		String s_point = null;
		if (pointPos >= 0) {
			s_int = numStr.substring(0, pointPos);
			s_point = numStr.substring(pointPos + 1);
		} else {
			s_int = numStr;
		}
		StringBuilder sb = new StringBuilder();
		if (!"0".equals(s_int)) {
			int groupCount = (int) Math.ceil(s_int.length() / 4.0);
			for (int group = 0; group < groupCount; group++) {
				boolean zeroFlag = true;
				boolean noZeroFlag = false;
				int start = (s_int.length() % 4 == 0 ? 0 : (s_int.length() % 4 - 4)) + 4 * group;
				for (int i = 0; i < 4; i++) {
					if (i + start >= 0) {
						int value = s_int.charAt(i + start) - '0';
						if (value > 0) {
							sb.append(CN_UPPER_NUMBER[value]);
							if (i < 3) {
								sb.append(CN_UPPER_UNIT[i]);
							}
							zeroFlag = true;
							noZeroFlag = true;
						} else if (zeroFlag) {
							sb.append('零');
							zeroFlag = false;
						}
					}
				}
				if (sb.charAt(sb.length() - 1) == '零') {
					sb.deleteCharAt(sb.length() - 1);
				}
				if (noZeroFlag || groupCount - group == 1) {
					sb.append(CN_GROUP[groupCount - group - 1]);
				}
			}
		}
		if (s_point == null || "00".equals(s_point)) {
			sb.append('整');
		} else {
			int j = s_point.charAt(0) - '0';
			int f = s_point.charAt(1) - '0';
			if (j > 0) {
				sb.append(CN_UPPER_NUMBER[j]).append('角');
				if (f != 0) {
					sb.append(CN_UPPER_NUMBER[f]).append('分');
				}
			} else if ("0".equals(s_int)) {
				sb.append(CN_UPPER_NUMBER[f]).append('分');
			} else {
				sb.append('零').append(CN_UPPER_NUMBER[f]).append('分');
			}
		}
		return sb.toString();

	}
}
