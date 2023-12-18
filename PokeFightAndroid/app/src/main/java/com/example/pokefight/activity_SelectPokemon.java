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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class activity_SelectPokemon extends AppCompatActivity {
    ArrayList<pokemon> pokemonsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pokemon);
        pokemonsList = (ArrayList<pokemon>) getIntent().getSerializableExtra("pokemonList");
        GridView gridView = (GridView) findViewById(R.id.listOfPokemons);

        gridView.setAdapter(new ImageAdapter(this, pokemonsList));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent returnIntent = new Intent();
                // Guarda el nombre del Pokémon en el Intent.
                pokemon selectedPokemon = pokemonsList.get(position);
                returnIntent.putExtra("selectedPokemon", selectedPokemon);
                int buttonId = getIntent().getIntExtra("buttonId", -1);
                returnIntent.putExtra("buttonId", buttonId);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private Integer[] mImageIds;
        private ArrayList<pokemon> pokemonList;
        public ImageAdapter(Context c, ArrayList<pokemon> pokemonsList) {
            mContext = c;
            this.pokemonList=pokemonsList;
        }

        @Override
        public int getCount() {
            return pokemonsList.size();
        }

        @Override
        public Object getItem(int position) {
            return pokemonsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
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

            imageView.setImageResource(getResources().getIdentifier(pokemonsList.get(position).getName().toLowerCase(), "drawable", getPackageName()));
            return imageView;
        }


    }
}


