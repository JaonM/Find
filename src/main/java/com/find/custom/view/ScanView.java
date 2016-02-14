package com.find.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.find.R;

public class ScanView extends View {

	private Bitmap scanBitmap;
	
	private Bitmap fingerprintBitmap;
	
	private boolean isScanning=false;
	
    private Context mContext;

//	private int widthPixel;
//	private int heightPixel;
	
	//Canvas旋转角度
	private int rotateDegree=0;

    //触摸持续时间
    private long pressTime;

    //请求回调接口
    private Callable mCallable;

    //接口是否回调
    private boolean isCalled=false;

    //触摸开始时间
    private long startTime=0;

	public ScanView(Context context) {
		super(context);
		mContext=context;
		if(scanBitmap==null)
			scanBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.scan_pic);
		if(fingerprintBitmap==null)
			fingerprintBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.fingerprint_not_pressed);

//		widthPixel=metrics.widthPixels;
//		heightPixel=metrics.heightPixels;
		
	}
	
	public ScanView(Context context,AttributeSet attrs) {
		super(context,attrs);
        mContext=context;
		if(scanBitmap==null)
			scanBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.scan_pic);
		if(fingerprintBitmap==null)
			fingerprintBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.fingerprint_not_pressed);

//		widthPixel=metrics.widthPixels;
//		heightPixel=metrics.heightPixels;


	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
//		Log.v("isScanning",isScanning+"");
		if(isScanning) {
			Rect rect=new Rect(getWidth()/2-scanBitmap.getWidth(),getHeight()/2,getWidth()/2,getHeight()/2+scanBitmap.getHeight());
				
			canvas.rotate(rotateDegree, getWidth()/2, getHeight()/2);
			canvas.drawBitmap(scanBitmap, null, rect, null);
			rotateDegree+=3;
			
			invalidate();
		} else {
//			canvas.restore();
			rotateDegree=0;
		}
		canvas.restore();
		canvas.drawBitmap(fingerprintBitmap, getWidth()/2-fingerprintBitmap.getWidth()/2,
				getHeight()/2-fingerprintBitmap.getHeight()/2,null);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		Log.v("action", event.getAction()+"");

		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			RectF rect=new RectF(getWidth()/2-fingerprintBitmap.getWidth()/2,getHeight()/2-fingerprintBitmap.getHeight()/2,
					getWidth()/2+fingerprintBitmap.getWidth()/2,getHeight()/2+fingerprintBitmap.getHeight()/2);
			if(rect.contains(event.getX(), event.getY())) {
				isScanning=true;
                startTime=System.currentTimeMillis();

				invalidate();
			}
			break;
        case MotionEvent.ACTION_MOVE:
            pressTime=System.currentTimeMillis()-startTime;
            if(pressTime>1000&&mCallable!=null&&!isCalled&&isScanning) {
                mCallable.call();
                isCalled=true;
            }
            break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
            /**
             * 监听触摸持续时间大于1秒，请求，小于1秒，提示
             */
//            Log.v("startTime",startTime+"");
            pressTime=System.currentTimeMillis()-startTime;
//            Log.v("pressTime",pressTime+"");
            if(pressTime<=1000&&isScanning) {
                Toast.makeText(mContext,"还要按久一点才能搜到人噢",Toast.LENGTH_SHORT).show();
            }
			isScanning=false;
			invalidate();
			break;
		}
		return true;
	}

    public void setCallable(Callable callable) {
        this.mCallable=callable;
    }

    public static interface Callable {
        public void call();
    }
}


