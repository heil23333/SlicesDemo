package fsc.com.slicesdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

public class PaletteActivity extends AppCompatActivity {
    View lightVibrantView, vibrant, darkVibrant, lightMuted, muted, darkMuted;
    ImageView imageView;
    final int RE_READ_EX = 100;
    final int RE_PICK_PHOTO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        final View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if (i == 0) {
                    new HideBarAsyncTask(decorView).execute();
                }
            }
        });

        Palette palette = Palette.from(BitmapFactory.decodeResource(getResources(), R.drawable.joker)).generate();
        setToolbarColor(palette);
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(PaletteActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PaletteActivity.this
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PaletteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE}, RE_READ_EX);
                } else {
                    selectPicture();
                }
            }
        });
        lightVibrantView = findViewById(R.id.dv_view);
        vibrant = findViewById(R.id.view2);
        darkVibrant = findViewById(R.id.view3);
        lightMuted = findViewById(R.id.view4);
        muted = findViewById(R.id.view5);
        darkMuted = findViewById(R.id.view6);

        lightVibrantView.setBackgroundColor(palette.getLightVibrantColor(Color.CYAN));
        vibrant.setBackgroundColor(palette.getVibrantColor(Color.CYAN));
        darkVibrant.setBackgroundColor(palette.getDarkVibrantColor(Color.CYAN));
        lightMuted.setBackgroundColor(palette.getLightMutedColor(Color.CYAN));
        muted.setBackgroundColor(palette.getMutedColor(Color.CYAN));
        darkMuted.setBackgroundColor(palette.getDarkMutedColor(Color.CYAN));
    }

    public void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, RE_PICK_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RE_READ_EX && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPicture();
        } else {
            Toast.makeText(this, "权限被拒绝，无法访问资源", Toast.LENGTH_LONG).show();
        }
    }

    public static void insertPath(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { path }, null);
        if (cursor != null && !cursor.moveToFirst()) {
            // 如果图片不在手机的共享图片数据库，就把它插入。
            if (new File(path).exists()) {
                System.out.println("hl-------");
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RE_PICK_PHOTO && data!= null && data.getData ()!= null) {
            Glide.with(this).asBitmap().load(data.getData()).into(new BitmapImageViewTarget(imageView) {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    super.onResourceReady(resource, transition);
                    Palette palette = Palette.from(resource).generate();
                    setToolbarColor(palette);
                    lightVibrantView.setBackgroundColor(palette.getLightVibrantColor(Color.CYAN));
                    vibrant.setBackgroundColor(palette.getVibrantColor(Color.CYAN));
                    darkVibrant.setBackgroundColor(palette.getDarkVibrantColor(Color.CYAN));
                    lightMuted.setBackgroundColor(palette.getLightMutedColor(Color.CYAN));
                    muted.setBackgroundColor(palette.getMutedColor(Color.CYAN));
                    darkMuted.setBackgroundColor(palette.getDarkMutedColor(Color.CYAN));
                }
            });
        }
    }

    @TargetApi(19)
    private String getFilePathFromUrl(Uri uri) {
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    // Set the background and text colors of a toolbar given a
// bitmap image to match
    public void setToolbarColor(Palette p) {
        // Generate the palette and get the vibrant swatch
        // See the createPaletteSync() method
        // from the code snippet above

        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();

        Toolbar toolbar = findViewById(R.id.toolbar);

        // Load default colors
        int backgroundColor = ContextCompat.getColor(this,
                R.color.colorPrimary);
        int textColor = ContextCompat.getColor(this,
                R.color.colorPrimaryDark);

        // Check that the Vibrant swatch is available
        if(vibrantSwatch != null){
            backgroundColor = vibrantSwatch.getRgb();
            textColor = vibrantSwatch.getTitleTextColor();
        }

        // Set the toolbar background and text colors
        toolbar.setBackgroundColor(backgroundColor);
        toolbar.setTitleTextColor(textColor);
    }
    static class HideBarAsyncTask extends AsyncTask<Void, Void, Void> {
        View view;

        public HideBarAsyncTask(View view) {
            this.view = view;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            view.setSystemUiVisibility(4);
            super.onPostExecute(aVoid);
        }
    }
}
