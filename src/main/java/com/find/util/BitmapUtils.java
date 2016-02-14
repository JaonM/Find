package com.find.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.find.R;

import java.lang.ref.WeakReference;

/**
 * Created by maqiang on 2015/2/10.
 */
public class BitmapUtils {

    private static final LruCache<String,Bitmap> mCache;       //将bitmap缓存之内存

    static {
        final int maxMemory=(int)(Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize=maxMemory/8;        //八分之一当做缓存大小
        mCache=new LruCache<String,Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key,Bitmap bitmap) {
                return bitmap.getByteCount()/1024;
            }
        };
    }


    private BitmapUtils() {

    }

    private static void addBitmapToCacheMemory(String key,Bitmap bitmap) {
        if(getBitmapFromCacheMemory(key)==null) {
            mCache.put(key,bitmap);
        }
    }

    private static Bitmap getBitmapFromCacheMemory(String key) {
        return mCache.get(key);
    }

    /**
     * 计算bitmap的缩放比
     */
    public static int calculateSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight) {
        int bitmapWidth=options.outWidth;
        int bitmapHeight=options.outHeight;
        int inSampleSize=1;
        if(bitmapWidth>reqWidth||bitmapHeight>reqHeight) {
            int halfWidth=bitmapWidth/2;
            int halfHeight=bitmapHeight/2;
            while((halfWidth/=inSampleSize)>reqWidth||(halfHeight/=inSampleSize)>reqHeight) {
                inSampleSize*=2;
            }
        }
        return inSampleSize;
    }

    /**
     * 从文件中读取Bitmap
     */
    public static Bitmap decodeSampleBitmapFromFile(String filePath,int reqWidth,int reqHeight) {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize=calculateSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFile(filePath,options);
    }

    /**
     * 异步任务获取图片
     * param[0]bitmap路径
     * param[1] String.valueOf(reqWidth)
     * param[2] String.valueOf(reqHeight)
     */
    public static class BitmapWorkerTask extends AsyncTask<String,Void,Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference=new WeakReference<>(imageView);
        }

        @Override
        public Bitmap doInBackground(String...params) {
            data=params[0];
            Bitmap bitmap=null;
            try {
                bitmap=decodeSampleBitmapFromFile(params[0], Integer.valueOf(params[1]), Integer.valueOf(params[2]));
                addBitmapToCacheMemory(data,bitmap);
            }catch(Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        public void onPostExecute(Bitmap bitmap) {
            if(isCancelled()) {
                bitmap=null;
            }
            if(bitmap!=null) {
                ImageView imageView=imageViewReference.get();
                BitmapWorkerTask task=getBitmapWorkerTask(imageView);
                if(imageView!=null&&this==task) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    /**
     * 自定义BitmapDrawable 控制并发
     */
    private static class AsyncTaskDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncTaskDrawable(Resources res,Bitmap bitmap,BitmapWorkerTask task) {
            super(res,bitmap);
            bitmapWorkerTaskReference=new WeakReference<>(task);
        }

        public  BitmapWorkerTask getBitmapWorkerTsk() {
            return bitmapWorkerTaskReference.get();
        }
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if(imageView!=null) {
            Drawable drawable=imageView.getDrawable();
            if(drawable instanceof AsyncTaskDrawable) {
                return ((AsyncTaskDrawable)drawable).getBitmapWorkerTsk();
            }
        }
        return null;
    }

    /**
     * 取消正在进行的任务
     * @param imageView
     * @param data
     * @return
     */
    private static boolean cancelPotentialTask(ImageView imageView,String data) {
        BitmapWorkerTask bitmapWorkerTask=getBitmapWorkerTask(imageView);
        if(bitmapWorkerTask!=null) {
            if(data==null||(bitmapWorkerTask.data!=null&&!bitmapWorkerTask.data.equals(data))) {
                bitmapWorkerTask.cancel(true);
            }else {
                return false;
            }
        }

        return true;
    }

    /**
     * 从文件中获取bitmap
     * @param imageView
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     */
    public static void loadBitmap(Context context,ImageView imageView,String filePath,int reqWidth,int reqHeight) {
        Bitmap bitmap=getBitmapFromCacheMemory(filePath);
        if(bitmap!=null) {
            imageView.setImageBitmap(bitmap);
        }else if(cancelPotentialTask(imageView,filePath)) {
            final BitmapWorkerTask task=new BitmapWorkerTask(imageView);
            final AsyncTaskDrawable drawable=new AsyncTaskDrawable(context.getResources(),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_picture_dark),
                    task);
            imageView.setImageDrawable(drawable);
            task.execute(filePath, String.valueOf(reqWidth), String.valueOf(reqHeight));
        }
    }
}
