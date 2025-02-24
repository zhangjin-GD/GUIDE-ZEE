package guide.app.common;

import java.util.ArrayList;
import java.util.List;

import guide.iface.webservice.appmenu.MenuTree;

public class TreeUtil {

	public static <T> MenuTree<T> buildMenuTree(List<MenuTree<T>> nodes) {
		if (nodes == null) {
			return null;
		}
		List<MenuTree<T>> topNodes = new ArrayList<>();
		nodes.forEach(children -> {
			String pNum = children.getParent();
			// 没有父id
			if (pNum == null || "null".equals(pNum) || pNum.isEmpty()) {
				topNodes.add(children);
				return;
			}
			for (MenuTree<T> parent : nodes) {
				String num = parent.getNum();
				if (num != null && num.equals(pNum)) {
					parent.getChilds().add(children);
					children.setHasParent(true);
					parent.setHasChild(true);
					parent.setUser(false);
					return;
				}
			}
		});

		MenuTree<T> root = new MenuTree<>();
		root.setId("0");
		root.setParent("");
		root.setHasParent(false);
		root.setHasChild(true);
		root.setUser(false);
		root.setChecked(true);
		root.setChilds(topNodes);
		return root;
	}

}
