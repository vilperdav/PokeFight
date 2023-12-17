package com.example.pokefight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SelectPokemon extends AppCompatActivity {
    private Integer[] images = {

            R.drawable.charmander,
            R.drawable.squirtle,
            R.drawable.treecko,
            R.drawable.torchic,
            R.drawable.mudkip,
            R.drawable.turtwig,
            R.drawable.chimchar,
            R.drawable.piplup,
            // ... más imágenes ...
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pokemon);

        GridView gridView = (GridView) findViewById(R.id.my_grid_view);
        gridView.setAdapter(new ImageAdapter(this, images));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("selectedImage", images[position]);
                // Obtén el buttonId del Intent que inició esta actividad.
                int buttonId = getIntent().getIntExtra("buttonId", -1);

                // Pónlo en el Intent de retorno.
                returnIntent.putExtra("buttonId", buttonId);

                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private Integer[] mImageIds;

        public ImageAdapter(Context c, Integer[] imageIds) {
            mContext = c;
            mImageIds = imageIds;
        }

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(500, 500));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mImageIds[position]);
            return imageView;
        }
    }
}


