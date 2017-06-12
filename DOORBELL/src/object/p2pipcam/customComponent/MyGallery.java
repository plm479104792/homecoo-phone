package object.p2pipcam.customComponent;

import object.dbnewgo.client.ShowLocalPictureActivity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

public class MyGallery extends Gallery {
	private GestureDetector gestureScanner;
	private MyLocalPicImageView imageView;

	public MyGallery(Context context) {
		super(context);

	}

	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);

		gestureScanner = new GestureDetector(new MySimpleGesture());
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = MyGallery.this.getSelectedView();
				if (view instanceof MyLocalPicImageView) {
					imageView = (MyLocalPicImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = imageView.getScale();
						Log.d("tg", "originalScale:" + originalScale);
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (event.getPointerCount() == 2) {
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
								Log.d("tg", "MyGallery onTouch scale:" + scale);
								imageView.zoomTo(originalScale * scale, x
										+ event.getX(1), y + event.getY(1));

							}
						}
					}
				}
				return false;
			}

		});
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		View view = MyGallery.this.getSelectedView();
		if (view instanceof MyLocalPicImageView) {
			imageView = (MyLocalPicImageView) view;

			float v[] = new float[9];
			Matrix m = imageView.getImageMatrix();
			m.getValues(v);
			// 图片实时的上下左右坐标
			float left, right;
			// 图片的实时宽，高
			float width, height;
			width = imageView.getScale() * imageView.getImageWidth();
			height = imageView.getScale() * imageView.getImageHeight();
			// 一下逻辑为移动图片和滑动gallery换屏的逻辑。如果没对整个框架了解的非常清晰，改动以下的代码前请三思！！！！！！
			if ((int) width <= ShowLocalPictureActivity.screenWidth
					&& (int) height <= ShowLocalPictureActivity.screenHeight)// 如果图片当前大小<屏幕大小，直接处理滑屏事件
			{
				super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				left = v[Matrix.MTRANS_X];
				right = left + width;
				Rect r = new Rect();
				imageView.getGlobalVisibleRect(r);

				if (distanceX > 0)// 向左滑动
				{
					if (r.left > 0) {// 判断当前ImageView是否显示完全
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (right < ShowLocalPictureActivity.screenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				} else if (distanceX < 0)// 向右滑动
				{
					if (r.right < ShowLocalPictureActivity.screenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (left > 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				}

			}

		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// 判断上下边界是否越界
			View view = MyGallery.this.getSelectedView();
			if (view instanceof MyLocalPicImageView) {
				imageView = (MyLocalPicImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale()
						* imageView.getImageHeight();
				if ((int) width <= ShowLocalPictureActivity.screenWidth
						&& (int) height <= ShowLocalPictureActivity.screenHeight)// 如果图片当前大小<屏幕大小，判断边界
				{
					break;
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private boolean isFirst = true;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (isFirst) {
				Log.d("tgg", "单击事件");
				MotionEvent event = (MotionEvent) msg.obj;

				if (myGalleryEvent != null) {
					myGalleryEvent.myTouch(event);
				}
			} else {
				Log.d("tgg", "取消事件");
			}
		}
	};

	private class MySimpleGesture extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.d("tgg", "isFirst");
			isFirst = true;
			Message msg = handler.obtainMessage();
			msg.obj = e;
			handler.sendMessageDelayed(msg, 300);
			return super.onSingleTapUp(e);
		}

		// 按两下的第二下Touch down时触发
		public boolean onDoubleTap(MotionEvent e) {
			isFirst = false;
			Log.d("tgg", "OnDublieTap");
			View view = MyGallery.this.getSelectedView();
			if (view instanceof MyLocalPicImageView) {
				imageView = (MyLocalPicImageView) view;
				if (imageView.getScale() > imageView.getScaleRate()) {

					imageView.zoomTo(imageView.getScaleRate(),
							ShowLocalPictureActivity.screenWidth / 2,
							ShowLocalPictureActivity.screenHeight / 2, 200f);
				} else {
					imageView.zoomTo(1.0f,
							ShowLocalPictureActivity.screenWidth / 2,
							ShowLocalPictureActivity.screenHeight / 2, 200f);
				}

			} else {

			}
			return true;
		}
	}

	public void setMyTouch(MyGalleryEvent myGalleryEvent) {
		this.myGalleryEvent = myGalleryEvent;
	}

	private MyGalleryEvent myGalleryEvent = null;

	public interface MyGalleryEvent {
		abstract public void myTouch(MotionEvent event);
	}

}
