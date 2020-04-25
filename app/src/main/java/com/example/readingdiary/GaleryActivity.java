package com.example.readingdiary;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class GaleryActivity extends AppCompatActivity {
    String[] image_urls = {"https://avatars.mds.yandex.net/get-pdb/1220848/e8a6bf7c-a43a-4a02-b883-52a95ed38b9d/s1200?webp=false",
    "https://s1.1zoom.ru/big3/60/Foxes_Paws_Glance_506749.jpg",
    "https://avatars.mds.yandex.net/get-pdb/1365420/5d034df1-232a-4460-9b49-8e444d434b71/s1200?webp=false"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galery_activity);

        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, image_urls);
        viewPager.setAdapter(adapter);

    }
}
