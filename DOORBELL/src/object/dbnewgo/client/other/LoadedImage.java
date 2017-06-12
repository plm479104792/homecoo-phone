/**
 * 
 */
package object.dbnewgo.client.other;

import android.graphics.Bitmap;

/**
 * @author : ÕÔ¹¢»³
 * @version £º2012-12-28 ÏÂÎç2:11:35
 */
public class LoadedImage {
	Bitmap mBitmap;

	LoadedImage(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}
}
