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
import androidx.recyclerview.widget.SortedList;

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
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class GaleryActivity extends AppCompatActivity {
    private ImageView imageView;
    private final int Pick_image = 1;
    private RecyclerView galeryView;;
    private GaleryRecyclerViewAdapter adapter;
    private GaleryFullViewAdapter adapter1;
    private List<Bitmap> images;
    private List<String> names;
    private final int FULL_GALERY_CODE = 8800;

    private int count = 3;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galery_activity);
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        images = new ArrayList<>(); // список bitmap изображений
        names = new ArrayList<>(); // список путей к изображениями в файловой системе
        File fileDir1 = getApplicationContext().getDir("images" + File.pathSeparator + id, MODE_PRIVATE); // путь к папке с изображениями
        File[] files = fileDir1.listFiles(); // список файлов в папке
        if (files != null){
            for (int i = 0; i < files.length; i++){
                images.add(BitmapFactory.decodeFile(files[i].getAbsolutePath()));
                names.add(files[i].getAbsolutePath());
            }
        }

        galeryView = (RecyclerView) findViewById(R.id.galery_recycle_view);
        adapter = new GaleryRecyclerViewAdapter(images, getApplicationContext());

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3); // отображение изображений в 3 колонки
        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        galeryView.setAdapter(adapter);
        galeryView.setLayoutManager(layoutManager);

        galeryView.setItemAnimator(itemAnimator);


        // при нажатии на изображение переходим в активность с полным изображением
        adapter.setOnItemClickListener(new GaleryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(GaleryActivity.this, GaleryFullViewActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("position", position);
                startActivityForResult(intent, FULL_GALERY_CODE);

            }
        });




        Button pickImage = (Button) findViewById(R.id.button);

        // Выбор изображений из галереи
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Pick_image);
            }
        });
    }



    // методы проверки размера изображения до открытия. Если размер слишком большой - сжимаем
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Pick_image:
                if (resultCode == RESULT_OK) {
                    try {
                        // Обработка выбранного изображения из галереи
                        final Uri imageUri = data.getData();
                        int px = 600;
                        Bitmap bitmap = decodeSampledBitmapFromResource(imageUri, px, px); // файл сжимается
                        GregorianCalendar calendar = new GregorianCalendar();

//                        File file = new File(getApplicationContext().getExternalFilesDir(null) + "/images/" + id +"/" + calendar.getTimeInMillis() + ".png"); // на

                        File fileDir1 = getApplicationContext().getDir("images" + File.pathSeparator + id, MODE_PRIVATE);
                        File file2 = new File(fileDir1, calendar.getTimeInMillis() + ".png"); // создаем файл в директории изображений записи. Имя выбирается на основе времени.
                        file2.createNewFile();
                        OutputStream stream = null;
                        stream = new FileOutputStream(file2);


                        bitmap.compress(Bitmap.CompressFormat.PNG, 60, stream);// сохранение
                        stream.close();

                        // добавление изображения в список изображений
                        String name = file2.getAbsolutePath();
                        int pos = names.size();
                        int n = names.size();

                        for (int i = 0; i < n; i++){
                            Log.d("COMPARE1", names.get(i).compareTo(name) + " " + n + " " + i);
                            if (names.get(i).compareTo(name) > 0){
                                Log.d("COMPARE1", "! " + names.get(i).compareTo(name) + " " + n);
                                pos = i;
                                break;
                            }
                        }

                        images.add(pos, bitmap);
                        names.add(pos, name);


                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.d("IMAGE1", e.toString());
                    }
                }
                break;
            case FULL_GALERY_CODE:
                // Вернулись из показа полных изображений. Если там удалили изображение, то меняем список имен и изображений
                Log.d("RESULT1", "ok3");

                if (resultCode == RESULT_OK) {
                    Log.d("RESULT1", "ok4");

//                    int i = 0;

                    List<Integer> index = new ArrayList<>();
                    for (int i = 0; i < names.size(); i++){
                        if(!(new File(names.get(i)).exists())) {
                            index.add(i);
                        }
                    }

                    int minus = 0;
                    for (int i : index){
                        names.remove(i - minus);
                        images.remove(i - minus);
                        minus++;
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
                default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }


    }
}
