package object.dbnewgo.client.other;

/**
 * @author : 赵耿怀
 * @version ：2012-12-28 上午10:49:01 
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Findfile {
	List<String> list = new ArrayList<String>();

	public Findfile(String init) {
		System.out.println(init);
		start(init); // 开始查找 是传进来的参数
	}

	public void start(String yiji) {
		File file = new File(yiji);
		if (file.exists() && file.canRead()) // 这一步是用于linux下的，所以都要判断下是否能读
		{
			File[] files = new File[] {};
			files = file.listFiles();
			if (files.length == 0)
				return;// 返回,如果一级都为空的时候，就直接返回！
			for (int i = 0; i < files.length; i++) {

				list.add(files[i].getAbsolutePath());
				// 如果是jpg和png就加入list！

				if (files[i].isDirectory() && files[i].canRead()) {
					Find(files[i].getAbsolutePath());
					// 如果是目录，就用Find方法去找！ 进入第二级
				}
			}
		} else {
			return;
		}

	}

	public List<String> getList() {
		return list;
	}

	public int getSize() {
		return list.size();
	}

	public void Find(String xx) {
		File file = new File(xx);
		if (file.exists()) {
			File[] files = new File[] {};
			files = file.listFiles();
			if (files.length == 0)
				return; // 我用循环查看的时候，看是否是空的，如果是空的，就直接返回进入下一个
			for (int i = 0; i < files.length; i++) {

				list.add(files[i].getAbsolutePath());

				if (files[i].isDirectory() && files[i].canRead()) // 有权限，所以判断下是否能读
				{
					Find(files[i].getAbsolutePath());
					// 用的是递归的方法，如果是目录的话，就进入下一级！ 再进入.... N级
				}
			}
		} else {
			return;
		}
	}
}
