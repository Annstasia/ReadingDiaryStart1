package com.example.readingdiary;

import android.graphics.Matrix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class GaleryActivity extends AppCompatActivity {
    private ImageView imageView;
    private final int Pick_image = 1;
    private RecyclerView galeryView;;
    private GaleryRecyclerViewAdapter adapter;
    private GaleryFullViewAdapter adapter1;
    private List<Bitmap> images;
    private int count = 3;
    String id;
//    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galery_activity);


        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        images = new ArrayList<>();
        File fileDir1 = getApplicationContext().getDir("images" + File.pathSeparator + id, MODE_PRIVATE);
        File[] files = fileDir1.listFiles();
        if (files != null){
            Log.d("FILE3", "NOTNULL" + files.length);
            for (int i = 0; i < files.length; i++){
                images.add(BitmapFactory.decodeFile(files[i].getAbsolutePath()));
                Log.d("FILE3", "name " + files[i].getName());
                Log.d("FILE3", "bitmap " + BitmapFactory.decodeFile(files[i].getAbsolutePath()));
            }
        }

//        FileInputStream fileInputStream = getApplicationContext().openFileInput("/images/);
//        Bitmap source = BitmapFactory.decodeStream(fileInputStream);
//        images.add(source);
        imageView = (ImageView) findViewById(R.id.imageView);
        galeryView = (RecyclerView) findViewById(R.id.galery_recycle_view);
        adapter = new GaleryRecyclerViewAdapter(images, getApplicationContext());
//        adapter1 = new GaleryFullViewAdapter(images, getApplicationContext());


        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        galeryView.setAdapter(adapter);
//        galeryView.setAdapter(adapter1);

        galeryView.setLayoutManager(layoutManager);
//        galeryView.setLayoutManager(layoutManager1);


        galeryView.setItemAnimator(itemAnimator);


        adapter.setOnItemClickListener(new GaleryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                adapter1 = new GaleryFullViewAdapter(images, getApplicationContext());
                galeryView.setAdapter(adapter1);
                galeryView.setLayoutManager(layoutManager1);
                layoutManager1.scrollToPosition(position);
            }
        });




        Button pickImage = (Button) findViewById(R.id.button);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(NoteActivity.this, GaleryActivity.class);
//                startActivity(intent);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Pick_image);
            }
        });
    }


    public Bitmap decodeSampledBitmapFromResource(Uri imageUri,
                                                  int reqWidth, int reqHeight) throws Exception{

        // Читаем с inJustDecodeBounds=true для определения размеров
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = getContentResolver().openInputStream(imageUri);
        BitmapFactory.decodeStream(imageStream, null, options);

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false;
        imageStream.close();
        imageStream = getContentResolver().openInputStream(imageUri);
        return BitmapFactory.decodeStream(imageStream, null, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Реальные размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d("SCALE", "OPTIONS " + height + " "  + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Вычисляем наибольший inSampleSize, который будет кратным двум
            // и оставит полученные размеры больше, чем требуемые
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case Pick_image:
                if (resultCode == RESULT_OK) {
                    try {
                        //объект и отображаем в элементе ImageView нашего интерфейса:
                        final Uri imageUri = imageReturnedIntent.getData();
//                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);

//                        imageUri.getPath()
                        int px = 600;
//                        File file = new File(Environment.
//                                getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"map.jpg");
                        Log.d("SCALE", "" +imageUri.getPath());

                        Bitmap bitmap = decodeSampledBitmapFromResource(imageUri, px, px);

                        Log.d("SCALE", "" + bitmap);
//                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                         String currentDateandTime = sdf.format(new Date());
                        GregorianCalendar calendar = new GregorianCalendar();
//                        calendar.setTime(new Date());
//                        Date currentDate = new Date()
//                        String ms = calendar.get(Calendar.MILLISECOND);
////                        calendar.setTime(new Date());
//                        String ss = calendar.get(Calendar.SECOND);
//                        String mt = calendar.get(Calendar.MINUTE);
//                        String hh = calendar.get(Calendar.HOUR_OF_DAY);
//                        String dd = calendar.get(Calendar.DAY_OF_MONTH);
//                        String mm = calendar.get(Calendar.MONTH);
//                        String yy = calendar.get(Calendar.YEAR);

//                        String name = yy + "_" + mm + "_" + dd + "_" + hh + "_" + mt + "_" + ss + "_" + ms;
//                        String seconds = calendar.get(Calendar.SECOND).toString();

//                        Log.d("TIME11", calendar.getTimeInMillis() + "");
                        Log.d("TIME11", "giu");
                        File file = new File(getApplicationContext().getExternalFilesDir(null) + "/images/" + id +"/" + calendar.getTimeInMillis() + ".png");
                        Log.d("FILE3", file.getAbsolutePath());


                        File fileDir1 = getApplicationContext().getDir("images" + File.pathSeparator + id, MODE_PRIVATE);
                        File file2 = new File(fileDir1, calendar.getTimeInMillis() + ".png");
                        Log.d("FILE3", "?");

                        file2.createNewFile();
                        Log.d("FILE3", "!!!!?!");
                        OutputStream stream = null;
                        try{
                            stream = new FileOutputStream(file2);
//                            stream = getApplicationContext().openFileOutput(fileDir1 + File.pathSeparator + calendar.getTimeInMillis() + ".png", MODE_PRIVATE);
//
                        }
                        catch (Exception e){
                            Log.d("File3", "?" + e.toString());
                        }
                        Log.d("FILE3", "AfterStream");
//                        Log.d("TIME11", stream.toString());
                        Log.d("TIME11", "giu");


                        bitmap.compress(Bitmap.CompressFormat.PNG, 60, stream);// пишем битмап на PNG с качеством 70%
                        stream.close();

                        File[] files = fileDir1.listFiles();
                        if (files != null){
                            Log.d("FILE3", "NOTNULL" + files.length);
                            for (int i = 0; i < files.length; i++){
                                images.add(BitmapFactory.decodeFile(files[i].getAbsolutePath()));
                                Log.d("FILE3", "name " + files[i].getName());
                                Log.d("FILE3", "bitmap " + BitmapFactory.decodeFile(files[i].getAbsolutePath()));
                            }
                        }
//
//                        FileInputStream fileInputStream = getApplicationContext().openFileInput("image" + count + ".png");
//                        Bitmap source = BitmapFactory.decodeStream(fileInputStream);
//                        images.add(source);
//                        Display display = getWindowManager().getDefaultDisplay();
//                        DisplayMetrics metricsB = new DisplayMetrics();
//                        display.getMetrics(metricsB);
//                        float size = metricsB.widthPixels / 3;
//                        float size1 = Math.min(source.getWidth(), source.getHeight());
//                        float k = size / size1;
//                        Log.d("SCALE", k+" " + size + " " + size1);
//                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, (int)(source.getWidth() * k), (int)(source.getHeight() * k), false);
//                        int x = (int)((resizedBitmap.getWidth() - size) / 2);
//                        int y = (int)((resizedBitmap.getHeight() - size) / 2);
//                        Bitmap result = Bitmap.createBitmap(resizedBitmap, x, y, (int)size, (int)size);
//                        images.add(result);

                        adapter.notifyDataSetChanged();
//                        adapter1.notifyDataSetChanged();

//                        count++;
//                        fileInputStream.close();;

                    } catch (Exception e) {
                        Log.d("IMAGE1", e.toString());
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }
}
