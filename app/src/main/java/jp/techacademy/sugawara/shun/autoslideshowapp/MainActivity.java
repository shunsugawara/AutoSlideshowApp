package jp.techacademy.sugawara.shun.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    ImageView mImageView;
    long mId;
    Timer mTimer;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ButtonのIDを取得する
        final Button nextButton = (Button) findViewById(R.id.nextButton);
        final Button backButton = (Button) findViewById(R.id.backButton);
        final Button autoButton = (Button) findViewById(R.id.autoButton);
        mImageView = (ImageView) findViewById(R.id.imageView);

        //requestpermission（許可が取れていない場合にリクエストする）
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                setFirstImage();
            }else{
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        }


        //nextButton
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextImageShow();
            }
        });
        //backButton
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                backImageShow();
            }
        });

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTimer==null){
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    nextImageShow();
                                }
                            });
                        }
                    },2000,2000);

                    nextButton.setEnabled(false);
                    backButton.setEnabled(false);
                    autoButton.setText("停止");
                }else{
                    mTimer.cancel();
                    mTimer = null;
                    nextButton.setEnabled(true);
                    backButton.setEnabled(true);
                    autoButton.setText("再生");
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults){
        switch(requestCode){
            case PERMISSIONS_REQUEST_CODE:
                //許可されている場合は、imageViewに画像をセットする。許可されない場合は、再度要求する
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setFirstImage();
                }else{
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
                }
                break;
            default:
                break;

        }
    }


    private void setFirstImage(){
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,null,null,null
        );

        if(cursor.moveToFirst()){
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                mId = cursor.getLong(fieldIndex);
                Uri imageURI = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mId);
                Log.d("ANDROID","URI:"+imageURI.toString());
                mImageView.setImageURI(imageURI);
        }else{
            Log.d("ANDROID","表示できる画像がない");
        }
        cursor.close();

    }

    private void nextImageShow(){
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,null,null,null
        );

        if(cursor.moveToFirst()){
            do {
                int checkFieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long checkId = cursor.getLong(checkFieldIndex);

                if(mId == checkId){
                    if(cursor.moveToNext()){

                    }else{
                        cursor.moveToFirst();
                    }
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    mId = cursor.getLong(fieldIndex);
                    Uri imageURI = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mId);
                    Log.d("ANDROID","URI"+imageURI.toString());
                    mImageView.setImageURI(imageURI);
                }

            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    private void backImageShow(){
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,null,null,null
        );

        if(cursor.moveToFirst()){
            do {
                int checkFieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long checkId = cursor.getLong(checkFieldIndex);

                if(mId == checkId){
                    if(cursor.moveToPrevious()){

                    }else{
                        cursor.moveToLast();
                    }

                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    mId = cursor.getLong(fieldIndex);
                    Uri imageURI = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mId);
                    Log.d("ANDROID","URI"+imageURI.toString());
                    mImageView.setImageURI(imageURI);
                }

            }while(cursor.moveToNext());
        }
        cursor.close();
    }

}
