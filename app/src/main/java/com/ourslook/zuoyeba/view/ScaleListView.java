package com.ourslook.zuoyeba.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.utils.DisplayUtils;

@SuppressLint("HandlerLeak")
public class ScaleListView extends ListView {
	
	private Context context;
	private View view_scale;// 缩放控件
	private FrameLayout.LayoutParams params;// 缩放控件的参数
	
	private int minY = 150;// 最小高度
	private int maxY = 300;// 最大高度
	private int eachY = 0;// 每次缩小的距离
	int y1 = 0;// 按下时记录的坐标
	int y2 = 0;// 移动时记录的坐标
	int temp = 40;// 触发拉伸的移动长度
	
	private boolean isIn = true;// 是否缩放
	private boolean isItemFirst = false;// listView是否滚动到第一项

	public ScaleListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initAtrrs(attrs);
		init();
	}
	
	public ScaleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initAtrrs(attrs);
		init();
	}
	
	/**
	 * 初始化自定义属性
	 * @param attrs
	 */
	private void initAtrrs(AttributeSet attrs){
		TypedArray a = context.obtainStyledAttributes(attrs,  
                R.styleable.ScaleListView);
		minY = a.getInt(R.styleable.ScaleListView_scale_minY, 150); 
		maxY = a.getInt(R.styleable.ScaleListView_scale_maxY, 300);
		a.recycle();
	}

	/**
	 * 初始化
	 */
	@SuppressLint("ClickableViewAccessibility")
	private void init() {
		temp = DisplayUtils.dp2px(temp,context );
		minY = DisplayUtils.dp2px(minY,context);
		maxY = DisplayUtils.dp2px(maxY,context);
		eachY = (maxY - minY) / 30;
		
		this.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 判断滚动到底部
					if (ScaleListView.this.getLastVisiblePosition() == (ScaleListView.this
							.getCount() - 1)) {
					}
					// 判断滚动到顶部
					if (ScaleListView.this.getFirstVisiblePosition() == 0) {
						isItemFirst = true;
					} else {
						isItemFirst = false;
					}
					break;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {}
		});
		
		this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					handler.sendEmptyMessage(3);
					y1 = (int) event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					y2 = (int) event.getY();
					if (y2 - y1 > temp && isItemFirst) {
						handler.sendEmptyMessage(1);
					} else if (y1 - y2 > temp ) {
						handler.sendEmptyMessage(2);
					}
					break;
				case MotionEvent.ACTION_UP:
					handler.sendEmptyMessage(3);
					break;
				default:
					break;
				}
				return false;
			}
		});
	}

	/**
	 * 设置缩放控件
	 * @param view_scale
	 */
	public void setScaleView(View view_scale) {
		this.view_scale = view_scale;
		params = (FrameLayout.LayoutParams) this.view_scale.getLayoutParams();
	}

	/**
	 * 设置最小高度
	 * @param minY
	 */
	public void setMinY(int minY) {
		minY = DisplayUtils.dp2px(minY,context);
		this.minY = minY;
	}

	/**
	 * 设置最大高度
	 * @param maxY
	 */
	public void setMaxY(int maxY) {
		maxY = DisplayUtils.dp2px(maxY,context);
		this.maxY = maxY;
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:// 放大
				if (!isIn) {
					ScaleListView.this.setSelection(0);
					if (params.height < maxY) {
						params.height += eachY;
						if (params.height >= maxY) {
							params.height = maxY;
						}
						view_scale.setLayoutParams(params);
						handler.sendEmptyMessageDelayed(1, 20);
					}
				}
				break;
			case 2:// 缩小
				if (isIn) {
					ScaleListView.this.setSelection(0);
					if (params.height > minY) {
						params.height -= eachY;
						if (params.height <= minY) {
							params.height = minY;
						}
						view_scale.setLayoutParams(params);
						handler.sendEmptyMessageDelayed(2, 20);
					}
				}
				break;
			case 3://
				if (params.height == maxY) {
					isIn = true;
				} else if (params.height == minY) {
					isIn = false;
				}
				break;
			default:
				break;
			}

		};
	};
	

}
