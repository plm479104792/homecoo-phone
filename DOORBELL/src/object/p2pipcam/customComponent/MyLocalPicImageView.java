package object.p2pipcam.customComponent;

import object.dbnewgo.client.ShowLocalPictureActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

public class MyLocalPicImageView extends ImageView {
	@SuppressWarnings("unused")
	private static final String TAG = "ImageViewTouchBase";
	protected Matrix mBaseMatrix = new Matrix();
	protected Matrix mSuppMatrix = new Matrix();
	private final Matrix mDisplayMatrix = new Matrix();
	private final float[] mMatrixValues = new float[9];
	protected Bitmap image = null;
	int mThisWidth = -1, mThisHeight = -1;
	float mMaxZoom = 2.0f;// 最大缩放比例
	float mMinZoom;// 最小缩放比例
	private int imageWidth;// 图片的原始宽度
	private int imageHeight;// 图片的原始高度

	private float scaleRate;// 图片适应屏幕的缩放比例

	public MyLocalPicImageView(Context context, int imageWidth, int imageHeight) {
		super(context);
		Log.d("test", "ImageWidth:" + imageWidth + "  ImageHeight:"
				+ imageHeight);
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		if (imageWidth > 640 && imageHeight > 480) {
			mMaxZoom = 1.5f;
		} else if (imageWidth > 320 && imageWidth <= 640 && imageHeight > 240
				&& imageHeight <= 480) {
			mMaxZoom = 2.0f;
		} else {
			mMaxZoom = 2.0f;
		}
		init();
	}

	public MyLocalPicImageView(Context context, AttributeSet attrs,
			int imageWidth, int imageHeight) {
		super(context, attrs);
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		init();
	}

	private void arithScaleRate() {
		float scaleWidth = ShowLocalPictureActivity.screenWidth
				/ (float) imageWidth;
		float scaleHeight = ShowLocalPictureActivity.screenHeight
				/ (float) imageHeight;
		scaleRate = Math.min(scaleWidth, scaleHeight);
	}

	public float getScaleRate() {
		return scaleRate;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
				&& !event.isCanceled()) {
			if (getScale() > 1.0f) {
				zoomTo(1.0f);
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	protected Handler mHandler = new Handler();

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		image = bitmap;
		// 计算适应屏幕的比例
		arithScaleRate();
		// 缩放到屏幕大小
		zoomTo(scaleRate, ShowLocalPictureActivity.screenWidth / 2f,
				ShowLocalPictureActivity.screenHeight / 2f);

		// 居中
		layoutToCenter();
	}

	protected void center(boolean horizontal, boolean vertical) {
		if (image == null) {
			return;
		}

		Matrix m = getImageViewMatrix();

		RectF rect = new RectF(0, 0, image.getWidth(), image.getHeight());

		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			int viewHeight = getHeight();
			if (height < viewHeight) {
				deltaY = (viewHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < viewHeight) {
				deltaY = getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int viewWidth = getWidth();
			if (width < viewWidth) {
				deltaX = (viewWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < viewWidth) {
				deltaX = viewWidth - rect.right;
			}
		}

		postTranslate(deltaX, deltaY);
		setImageMatrix(getImageViewMatrix());
	}

	private void init() {
		setScaleType(ImageView.ScaleType.MATRIX);
	}

	/**
	 * 设置图片居中显示
	 */
	public void layoutToCenter() {
		// 正在显示的图片实际宽高
		float width = imageWidth * getScale();
		float height = imageHeight * getScale();
		Log.d("tag", "实际宽度:" + width);
		Log.d("tag", "实际高度:" + height);
		// 空白区域宽高
		float fill_width = ShowLocalPictureActivity.screenWidth - width;
		float fill_height = ShowLocalPictureActivity.screenHeight - height;

		// 需要移动的距离
		float tran_width = 0f;
		float tran_height = 0f;

		if (fill_width > 0)
			tran_width = fill_width / 2;
		if (fill_height > 0)
			tran_height = fill_height / 2;

		postTranslate(tran_width, tran_height);
		setImageMatrix(getImageViewMatrix());
	}

	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		mMinZoom = (ShowLocalPictureActivity.screenWidth / 2f) / imageWidth;

		return mMatrixValues[whichValue];
	}

	// Get the scale factor out of the matrix.
	protected float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}

	protected float getScale() {
		return getScale(mSuppMatrix);
	}

	protected Matrix getImageViewMatrix() {
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}

	static final float SCALE_RATE = 1.25F;

	protected float maxZoom() {
		if (image == null) {
			return 1F;
		}

		float fw = (float) image.getWidth() / (float) mThisWidth;
		float fh = (float) image.getHeight() / (float) mThisHeight;
		float max = Math.max(fw, fh) * 4;
		return max;
	}

	protected void zoomTo(float scale, float centerX, float centerY) {
		Log.d("tg", "zoomTo scale:" + scale);
		if (scale > mMaxZoom) {
			scale = mMaxZoom;
			Log.d("tag", "img max scale:" + scale);
		} else if (scale < mMinZoom) {
			scale = mMinZoom;
			Log.d("tag", "img min scale:" + scale);
		}

		float oldScale = getScale();
		Log.d("tg", "oldScale:" + oldScale);
		float deltaScale = scale / oldScale;
		Log.d("tag", "deltaScale:" + deltaScale);
		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	protected void zoomTo(final float scale, final float centerX,
			final float centerY, final float durationMs) {
		final float incrementPerMs = (scale - getScale()) / durationMs;
		final float oldScale = getScale();
		final long startTime = System.currentTimeMillis();

		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				float target = oldScale + (incrementPerMs * currentMs);
				zoomTo(target, centerX, centerY);
				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		center(true, true);// 解决了横竖屏切换不居中的问题的问题
		super.onDraw(canvas);
	}

	protected void zoomTo(float scale) {
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;
		zoomTo(scale, cx, cy);
	}

	protected void zoomToPoint(float scale, float pointX, float pointY) {
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		panBy(cx - pointX, cy - pointY);
		zoomTo(scale, cx, cy);
	}

	protected void zoomIn(float rate) {
		Log.d("tg", "zoomIn");
		if (getScale() >= mMaxZoom) {
			return;
		} else if (getScale() <= mMinZoom) {
			return;
		}
		if (image == null) {
			return;
		}

		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		mSuppMatrix.postScale(rate, rate, cx, cy);
		setImageMatrix(getImageViewMatrix());
	}

	protected void zoomOut(float rate) {
		if (image == null) {
			return;
		}
		Log.d("tag", "zoomOut:");
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		// Zoom out to at most 1x.
		Matrix tmp = new Matrix(mSuppMatrix);
		tmp.postScale(1F / rate, 1F / rate, cx, cy);

		if (getScale(tmp) < 1F) {
			mSuppMatrix.setScale(1F, 1F, cx, cy);
		} else {
			mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
		}
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	public void postTranslate(float dx, float dy) {
		mSuppMatrix.postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}

	float _dy = 0.0f;

	protected void postTranslateDur(final float dy, final float durationMs) {
		_dy = 0.0f;
		final float incrementPerMs = dy / durationMs;
		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);

				postTranslate(0, incrementPerMs * currentMs - _dy);
				_dy = incrementPerMs * currentMs;

				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}

	protected void panBy(float dx, float dy) {
		postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}
}