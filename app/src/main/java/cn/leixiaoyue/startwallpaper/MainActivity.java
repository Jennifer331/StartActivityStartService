package cn.leixiaoyue.startwallpaper;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    public static final int GET_IMAGE_REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

    public void chooseImage(View view) {
        startGalleryToChoose();
    }

    public static final String IMAGE_TYPE = "image/*";
    private void startGalleryToChoose() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GET_IMAGE_REQUEST_CODE);
    }

    private void simpleChoose() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(IMAGE_TYPE);
        startActivityForResult(Intent.createChooser(intent, "请选择图片以设置壁纸"), GET_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_IMAGE_REQUEST_CODE:
                Intent intent = new Intent();
                Uri uri = data.getData();
                Log.v("littleHappy", "uri.getEncodedPath" + uri.getEncodedPath());
                Log.v("littleHappy", "uri.getPath()" + uri.getPath());

//                Bitmap bitmap = loadBitmap(uri);

                startPreLoadService(uri);
//                Bitmap bitmap = loadBitmapByUri(uri);
//                saveBitmap(bitmap);

                intent.setAction("com.android.camera.action.CROP")
                .setDataAndType(uri, IMAGE_TYPE)
                .putExtra("set-as-wallpaper", true)
                .putExtra("noFaceDetection", true)
                .putExtra("scaleUpIfNeeded", true)
                .putExtra("scale", true);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
        }
    }

    private void startPreLoadService(Uri uri) {
        Intent intent = new Intent();
        intent.setData(uri);
        intent.setComponent(new ComponentName("com.**.**", ".***Service"));
        ComponentName c = startService(intent);
    }

    private Bitmap loadBitmapByUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    private Bitmap loadBitmap(Uri uri) {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            java.net.URI juri = new java.net.URI(uri.getScheme(),
                    uri.getSchemeSpecificPart(),
                    uri.getFragment());
            Log.v("littleHappy", "juri:" + juri);
            URLConnection conn = juri.toURL().openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    private void saveBitmap(Bitmap bitmap) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream("/sdcard/my.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            getApplication().getContentResolver().notifyChange(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
