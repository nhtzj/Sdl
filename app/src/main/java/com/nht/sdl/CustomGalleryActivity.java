package com.nht.sdl;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nht.sdl.base.BaseActivity;
import com.nht.sdl.gallery.Action;
import com.nht.sdl.gallery.CustomGallery;
import com.nht.sdl.gallery.GalleryAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;

public class CustomGalleryActivity extends BaseActivity {

    @BindView(R.id.gridGallery)
    GridView gridGallery;
    private Handler handler;
    private GalleryAdapter adapter;

    private String action;
    private ImageLoader imageLoader;

    private int maxPic;

    @Override
    protected int getLayoutId() {
        return R.layout.gallery;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.gallery;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        Intent intent = getIntent();
        action = intent.getAction();
        maxPic = intent.getIntExtra("maxPic", 9);
        if (action == null) {
            finish();
        }
    }

    @Override
    public void initView() {
        super.initView();
        initImageLoader();
    }

    @Override
    public void initData() {
        super.initData();

        handler = new Handler();
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
                true, true);
        gridGallery.setOnScrollListener(listener);

        if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK)) {

            gridGallery.setOnItemClickListener(mItemMulClickListener);
            adapter.setMultiplePick(true);

        } else if (action.equalsIgnoreCase(Action.ACTION_PICK)) {

            gridGallery.setOnItemClickListener(mItemSingleClickListener);
            adapter.setMultiplePick(false);

        }

        gridGallery.setAdapter(adapter);

//        btnGalleryOk.setOnClickListener(mOkClickListener);
//        btnGalleryCancel.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent data = new Intent().putExtra("all_path", new String[]{});
//                setResult(RESULT_OK, data);
//                finish();
//            }
//        });
//
//        btnBack.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent data = new Intent().putExtra("all_path", new String[]{});
//                setResult(RESULT_OK, data);
//                finish();
//            }
//        });

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.addAll(getGalleryPhotos());
//                        checkImageStatus();
                    }
                });
                Looper.loop();
            }
        }.start();
    }

    private void initImageLoader() {
        try {
//            String CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.temp_tmp";
//            File dir = new File(CACHE_DIR);
//            if (!dir.exists())
//                dir.mkdir();
//            Context context = getBaseContext();
//            File cacheDir = StorageUtils.getOwnCacheDirectory(context, CACHE_DIR);
//
//            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                    .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
//                    .bitmapConfig(Bitmap.Config.RGB_565).build();
//            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
//                    getBaseContext())
//                    .defaultDisplayImageOptions(defaultOptions)
//                    .discCache(new UnlimitedDiskCache(cacheDir))
//                    .memoryCache(new WeakMemoryCache());
//
//            ImageLoaderConfiguration config = builder.build();
            imageLoader = ImageLoader.getInstance();
//            imageLoader.init(config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                ArrayList<CustomGallery> selected = adapter.getSelected();
                if (selected.size() == 0) {
                    Intent intent = new Intent().putExtra("all_path", new String[]{});
                    setResult(RESULT_OK, intent);
                    finish();
                    return true;
                }
                String[] allPath = new String[selected.size()];
                for (int i = 0; i < allPath.length; i++) {
                    allPath[i] = selected.get(i).sdcardPath;
                }

                if (allPath.length > maxPic) {
                    Toast.makeText(getApplicationContext(), String.format("图片数量不能超过%d张，请重新选择", maxPic), Toast.LENGTH_SHORT).show();
                } else {
                    Intent data = new Intent().putExtra("all_path", allPath);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            adapter.changeSelection(v, position);

        }
    };

    AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            CustomGallery item = adapter.getItem(position);
            Intent data = new Intent().putExtra("single_path", item.sdcardPath);
            setResult(RESULT_OK, data);
            finish();
        }
    };

    private ArrayList<CustomGallery> getGalleryPhotos() {
        ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);
            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    CustomGallery item = new CustomGallery();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    item.sdcardPath = imagecursor.getString(dataColumnIndex);

                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

}
