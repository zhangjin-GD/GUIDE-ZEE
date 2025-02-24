package guide.iface.webservice.appmenu;

public class Menu {

	private String id;// id
	private String num;// 编号
	private String icon;// 图标
	private String brief;// 简介
	private String title;// 标题
	private String parent;// 父级
	private String processname;// 流程名
	private String url;// 路径

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getProcessname() {
		return processname;
	}

	public void setProcessname(String processname) {
		this.processname = processname;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
