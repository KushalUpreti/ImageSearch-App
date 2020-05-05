package com.example.imagesearch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagesearch.R;
import com.squareup.picasso.Picasso;

public class PhotoDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        Intent intent = getIntent();
        Photo photo = (Photo) intent.getSerializableExtra("Photo");
        if(photo!=null){
            TextView photoTitle = findViewById(R.id.photo_title);
            photoTitle.setText(photo.getTitle());

            TextView photoAuthor = findViewById(R.id.photo_author);
            photoAuthor.setText(photo.getAuthor());

            TextView photoTags = findViewById(R.id.photo_tags);
            photoTags.setText(photo.getTags());

            ImageView image = findViewById(R.id.photo_image);
            Picasso.with(this).load(photo.getLink())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(image);
            Toast.makeText(this,"Fullscreen Image Loaded",Toast.LENGTH_LONG).show();
        }
    }
}
