package guide.iface.sap.webservice;

import java.util.List;

public class SecondSapBean {

	public SecondHearBean getSecondHearBean() {
		return secondHearBean;
	}

	public void setSecondHearBean(SecondHearBean secondHearBean) {
		this.secondHearBean = secondHearBean;
	}

	public List<SecondItemBean> getSecondItemBeans() {
		return secondItemBeans;
	}

	public void setSecondItemBeans(List<SecondItemBean> secondItemBeans) {
		this.secondItemBeans = secondItemBeans;
	}

	private SecondHearBean secondHearBean;
	
	private List<SecondItemBean> secondItemBeans;

	
}
