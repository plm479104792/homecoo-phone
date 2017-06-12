package et.song.face;

import et.song.db.ETDB;
import et.song.etclass.ETGroup;

public interface IOp {
	public void Load(ETDB db);
	public void Update(ETDB db);
	public void Delete(ETDB db);
	public void Inster(ETDB db);
	public int GetCount();
	public Object GetItem(int i);
	public ETGroup findGroupByName(ETDB db);
}
