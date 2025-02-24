package guide.iface.webservice.appmenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuTree<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7766094605788473725L;

	private String id;// id
	private String num;// 编号
	private String icon;// 图标
	private String brief;// 简介
	private String title;// 标题
	private String processname;// 流程名
	private String url;// 路径
	private boolean checked = false;
	private List<MenuTree<T>> childs = new ArrayList<>();
	private String parent;
	private boolean hasParent = false;
	private boolean hasChild = false;
	private boolean user = true;

	private T data;

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

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public List<MenuTree<T>> getChilds() {
		return childs;
	}

	public void setChilds(List<MenuTree<T>> childs) {
		this.childs = childs;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean isHasParent() {
		return hasParent;
	}

	public void setHasParent(boolean hasParent) {
		this.hasParent = hasParent;
	}

	public boolean isHasChild() {
		return hasChild;
	}

	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}

	public boolean isUser() {
		return user;
	}

	public void setUser(boolean user) {
		this.user = user;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
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
