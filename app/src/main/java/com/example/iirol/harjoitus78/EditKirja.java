package com.example.iirol.harjoitus78;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iirol.harjoitus78.Database.Firebase.FirebaseDatabase;
import com.example.iirol.harjoitus78.Database.DatabaseException;
import com.example.iirol.harjoitus78.Database.Entities.Kirja;
import com.example.iirol.harjoitus78.Database.Firebase.FirebaseRepository;

public class EditKirja extends AppCompatActivity {

	// FIELDS
	private EditText numero;
	private EditText nimi;
	private EditText painos;
	private EditText hankintapvm;
	private Button tallenna;
	private Button peruuta;

	private FirebaseDatabase firebaseDatabase;
	private Kirja editableKirja;

	// METHODS
	private void getViews() {

		this.numero = this.findViewById(R.id.numero);
		this.nimi = this.findViewById(R.id.nimi);
		this.painos = this.findViewById(R.id.painos);
		this.hankintapvm = this.findViewById(R.id.hankintapvm);
		this.tallenna = this.findViewById(R.id.tallenna);
		this.tallenna.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EditKirja.this.tallenna_click(view);
			}
		});
		this.peruuta = this.findViewById(R.id.peruuta);
		this.peruuta.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EditKirja.this.peruuta_click(view);
			}
		});

	}
	private void tallenna_click(View view) {

		// Yritä tallettaa käyttäjän syöttämät tiedot olioon takaisin
		try {
			this.editableKirja.setNumero(Integer.valueOf(this.numero.getText().toString()));
			this.editableKirja.setNimi(this.nimi.getText().toString());
			this.editableKirja.setPainos(Integer.valueOf(this.painos.getText().toString()));
			this.editableKirja.setHankintapvm(this.hankintapvm.getText().toString());
		} catch (Exception dateException) {
			Toast.makeText(getApplicationContext(),"Annetuissa tiedoissa on jotain mätänä...", Toast.LENGTH_LONG).show();
			return;
		}

		// Disabloi napin painaminen
		view.setEnabled(false);

		this.firebaseDatabase.KirjaRepository.modify(this.editableKirja, new FirebaseRepository.ResultListener() {

			@Override public void onSuccess() {
				Toast.makeText(getApplicationContext(),"Kirja muokattiin tietokantaan onnistuneesti!", Toast.LENGTH_LONG).show();

				// Sulje sivu ja palaa kirjojen listaukseen
				EditKirja.this.finish();
			}

			@Override public void onError(DatabaseException repositoryException) {
				Toast.makeText(getApplicationContext(),"Jotakin kirjaa päivitettäessä tietokantaan meni pieleen...", Toast.LENGTH_LONG).show();

				// Sulje sivu ja palaa kirjojen listaukseen
				EditKirja.this.finish();
			}
		});

	}
	private void peruuta_click(View view) {
		this.finish();
	}
	private void listaaKirja() {
		this.numero.setText(String.valueOf(this.editableKirja.getNumero()));
		this.nimi.setText(this.editableKirja.getNimi());
		this.painos.setText(String.valueOf(this.editableKirja.getPainos()));
		this.hankintapvm.setText(this.editableKirja.getHankintapvm());
	}

	// @Activity
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_edit_kirja);
		this.getViews();

		// Lue lähetetty kirja
		this.editableKirja = getIntent().getExtras().getParcelable(Kirja.class.getSimpleName());

		this.firebaseDatabase = FirebaseDatabase.getInstance();

		// Listaa kirja editoitavaksi
		this.listaaKirja();
	}

}
