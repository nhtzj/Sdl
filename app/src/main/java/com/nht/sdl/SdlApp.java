package com.nht.sdl;

import android.app.Application;
import android.content.Context;

import com.nht.sdl.utils.MyImageLoader;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * Created by Haitao on 2016/8/6.
 */
public class SdlApp extends Application {

    public static MyImageLoader myImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        myImageLoader = MyImageLoader.build(getApplicationContext());
    }

    public void initImageLoader(Context context) {
        int NORM_PRIORITY = Thread.NORM_PRIORITY;
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCacheExtraOptions(480, 800)
                .threadPoolSize(4)
                .threadPriority(NORM_PRIORITY - 1 == 0 ? NORM_PRIORITY : NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(80 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
                .build();

        ImageLoader.getInstance().init(configuration);
        com.nostra13.universalimageloader.utils.L.disableLogging();
    }
}
