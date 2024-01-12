package com.example.pokefight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class activity_SelectPokemon extends AppCompatActivity {
    ArrayList<pokemon> pokemonsList;
    ListView commonList;

    /*
     * ********************************************************************************************
     * * onCreate                                                                                 *
     * ********************************************************************************************
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pokemon);

        // Get the data from pokemonList and set the new adapter
        pokemonsList = (ArrayList<pokemon>) getIntent().getSerializableExtra("pokemonList");
        commonList = (ListView) findViewById(R.id.listOfPokemon);
        commonList.setAdapter(new ImageAdapter(this, pokemonsList));
        commonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent returnIntent = new Intent();

                // Save the name of the pokemon in the intent and the buttonID
                pokemon selectedPokemon = pokemonsList.get(position);
                returnIntent.putExtra("selectedPokemon", selectedPokemon);
                returnIntent.putExtra("buttonId", getIntent().getIntExtra("buttonId", -1));

                // Send to the calling activity
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        String text = "¡Choose a pokemon!";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

    }

    /*
     * ********************************************************************************************
     * * ImageAdapter                                                                             *
     * ********************************************************************************************
     * */
    public class ImageAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<pokemon> pokemonList;

        public ImageAdapter(Activity activity, ArrayList<pokemon> pokemonsList) {
            this.activity = activity;
            this.pokemonList = pokemonsList;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inf.inflate(R.layout.activity_select_pokemon, null);
            }

            // Pokemon object
            pokemon poke = pokemonsList.get(position);

            // Objects from the frontEnd
            ImageView img = (ImageView) v.findViewById(R.id.pokeImg);
            TextView name = (TextView) v.findViewById(R.id.pokemonName);
            TextView type = (TextView) v.findViewById(R.id.pokemonType);
            TextView health = (TextView) v.findViewById(R.id.pokemonHealth);
            TextView atack = (TextView) v.findViewById(R.id.pokemonAttack);
            TextView defense = (TextView) v.findViewById(R.id.pokemonDefense);
            TextView speed = (TextView) v.findViewById(R.id.pokemonSpeed);

            // Update the image
            img.setImageResource(getResources().getIdentifier(poke.getName().toLowerCase(), "drawable", getPackageName()));

            // Update the name
            name.setText(poke.getName());

            // Update the type
            String pokeType = poke.getType();
            if (pokeType.equals("fire")) {
                name.setTextColor(ContextCompat.getColor(this.activity, R.color.LightRed));
            } else if (pokeType.equals("water")) {
                name.setTextColor(ContextCompat.getColor(this.activity, R.color.LightBlue));
            } else if (pokeType.equals("plant")) {
                name.setTextColor(ContextCompat.getColor(this.activity, R.color.LightGreen));
            }
            type.setText("Type: " + poke.getType().toUpperCase());

            // Update the rest of attributes
            health.setText("Health: " + poke.getHealth());
            atack.setText("Atack: " + poke.getAtack());
            defense.setText("Defense: " + poke.getDefense());
            speed.setText("Speed: " + poke.getSpeed());

            return v;
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

    }
}


