package com.example.iirol.harjoitus78;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iirol.harjoitus78.Database.Database;
import com.example.iirol.harjoitus78.Database.Repositories.Kirja.Kirja;
import com.example.iirol.harjoitus78.Database.Repositories.Kirja.KirjaRepository;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	// VARIABLES
	private static final int RC_SIGN_IN = 123;
	private FirebaseAuth firebaseAuth;

	private EditText numero;
	private EditText nimi;
	private EditText painos;
	private EditText hankintapvm;
	private Button addnew;
	private Button deletefirst;
	private TableLayout listaus;
	private TextView loggedUser;
	private Button logInOrOut;

	private SimpleDateFormat sdf;
	private Database database;

	// METHODS
	private void getViews() {

		this.numero = findViewById(R.id.numero);
		this.nimi = findViewById(R.id.nimi);
		this.painos = findViewById(R.id.painos);
		this.hankintapvm = findViewById(R.id.hankintapvm);
		this.addnew = findViewById(R.id.addnew);
		this.addnew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				addnew_click(view);
			}
		});
		this.deletefirst = findViewById(R.id.deletefirst);
		this.deletefirst.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				deletefirst_click(view);
			}
		});
		this.listaus = findViewById(R.id.listaus);
		this.loggedUser = findViewById(R.id.loggedUser);
		this.logInOrOut = findViewById(R.id.logInOrOut);
		this.logInOrOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				logInOrOut_click(view);
			}
		});

	}
	private void edit_click(View view) {

		// Jos käyttäjä ei ole kirjautunut, poistu ja ilmoita virhe
		if (FirebaseAuth.getInstance().getCurrentUser() == null) {
			Toast.makeText(this, "Ole hyvä ja kirjaudu ensin sisään!", Toast.LENGTH_LONG).show();
			return;
		}

		TableRow clickedTableRow = (TableRow)view;
		Kirja editableKirja = (Kirja)clickedTableRow.getTag();

		// Luo uusi bundle mihin kootaan välitettävät parametrit toiselle activitylle
		Bundle bundle = new Bundle();
		bundle.putInt(KirjaRepository.COLUMN_ID, editableKirja.getId());
		bundle.putInt(KirjaRepository.COLUMN_NUMERO, editableKirja.getNumero());
		bundle.putString(KirjaRepository.COLUMN_NIMI, editableKirja.getNimi());
		bundle.putInt(KirjaRepository.COLUMN_PAINOS, editableKirja.getPainos());
		bundle.putString(KirjaRepository.COLUMN_HANKINTAPVM, editableKirja.getHankintapvm());

		// Luo uusi intent millä siirrytään activitystä toiseen, ja lisää bundle siihen mukaan
		Intent intent = new Intent(this, EditKirja.class);
		intent.putExtras(bundle);

		// Siirry toiseen activityyn
		this.startActivity(intent);
	}
	private void addnew_click(View view) {

		// Numero
		String parsedStringNumero = this.numero.getText().toString();
		if (parsedStringNumero.isEmpty()) {
			Toast.makeText(getApplicationContext(),"Ole hyvä ja syötä 'Numero'!", Toast.LENGTH_LONG).show();
			return;
		}
		int parsedNumero = Integer.valueOf(parsedStringNumero);

		// Nimi
		String parsedNimi = this.nimi.getText().toString();
		if (parsedNimi.isEmpty()) {
			Toast.makeText(getApplicationContext(),"Ole hyvä ja syötä 'Nimi'!", Toast.LENGTH_LONG).show();
			return;
		}

		// Painos
		String parsedStringPainos = this.painos.getText().toString();
		if (parsedStringPainos.isEmpty()) {
			Toast.makeText(getApplicationContext(),"Ole hyvä ja syötä 'Painos'!", Toast.LENGTH_LONG).show();
			return;
		}
		int parsedPainos = Integer.valueOf(parsedStringPainos);

		// Hankinta pvm
		String parsedStringHankintapvm = this.hankintapvm.getText().toString();
		if (parsedStringHankintapvm.isEmpty()) {
			Toast.makeText(getApplicationContext(),"Ole hyvä ja syötä 'Hankinta pvm'!", Toast.LENGTH_LONG).show();
			return;
		}

		// Validoi päivämäärä
		try {
			this.sdf.parse(parsedStringHankintapvm);
		} catch (ParseException e) {
			Toast.makeText(getApplicationContext(),"Ole hyvä ja syötä 'Hankinta pvm' muodossa 'pp.kk.vvvv'!", Toast.LENGTH_LONG).show();
			return;
		}

		// Luo uusi kirja
		Kirja uusiKirja = new Kirja(parsedNumero, parsedNimi, parsedPainos, parsedStringHankintapvm);
		if (this.database.KirjaRepository.add(uusiKirja) > 0) {

			Toast.makeText(getApplicationContext(),"Uusi kirja lisätty!", Toast.LENGTH_LONG).show();

			// Päivitä näkymä
			this.listaaKirjat();

		} else {
			// Jotakin meni pieleen
			Toast.makeText(getApplicationContext(),"Jotakin meni pieleen uuden luonnissa...", Toast.LENGTH_LONG).show();
		}

	}
	private void deletefirst_click(View view) {

		// Poista ensimmäinen rivi tietokannasta
		if (this.database.KirjaRepository.deleteFirst()) {
			Toast.makeText(getApplicationContext(),"Ensimmäinen kirja poistettiin!", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(),"Kirjoja ei ole!", Toast.LENGTH_LONG).show();
		}

		// Päivitä näkymä
		this.listaaKirjat();
	}
	private void logInOrOut_click(View view) {

		if (FirebaseAuth.getInstance().getCurrentUser() != null) {

			AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
				public void onComplete(@NonNull Task<Void> task) {
					userLoginChanged();
				}
			});

		} else {

			// Choose authentication providers
			List<AuthUI.IdpConfig> providers = Arrays.asList(
					new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
					new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
			);

			// Create and launch sign-in intent
			this.startActivityForResult( AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN);

		}

	}

	private void userLoginChanged() {

		// Tarkasta onko käyttäjä kirjautunut sisään
		boolean loggedIn = (FirebaseAuth.getInstance().getCurrentUser() != null);

		// Syöttöpaneeli
		this.numero.setEnabled(loggedIn);
		this.nimi.setEnabled(loggedIn);
		this.painos.setEnabled(loggedIn);
		this.hankintapvm.setEnabled(loggedIn);
		this.addnew.setEnabled(loggedIn);
		this.deletefirst.setEnabled(loggedIn);

		// Listausta ei tarvitse muokata

		// Käyttäjäpaneeli
		this.loggedUser.setText(loggedIn ? FirebaseAuth.getInstance().getCurrentUser().getDisplayName() : "Ei kirjauduttuna sisään");
		this.logInOrOut.setText(loggedIn ? "Log out" : "Log in");

		// Päivitä näytettävät rivit
		this.listaaKirjat();

	}
	private void listaaKirjat() {

		// Poista kaikki nykyiset rivit
		this.listaus.removeAllViews();

		if (FirebaseAuth.getInstance().getCurrentUser() == null) {
			return;
		}

		// Hae kannasta kaikki kirjat nousevassa järjestyksessä päivämäärän mukaan
		ArrayList<Kirja> kirjat = this.database.KirjaRepository.getAll();

		// Käy jokainen löytynyt kirja läpi
		for (Kirja kirja : kirjat) {

			// Luo kirjalle oma teksti-elementtie UI:lle
			TextView textView = new TextView(this);
			textView.setText(kirja.toString());
			textView.setTextSize(17f);

			// Lisää rivi jonka sisälle teksti laitetaan
			TableRow tableRow = new TableRow(this);
			tableRow.setPadding(5, 5, 5, 5);
			tableRow.addView(textView);
			tableRow.setTag(kirja); // Lisää Kirja-olion referenssi 'TableRow'iin

			// Lisää "onclick"-kuuntelija riville kun sitä painaa, niin avataan toinen sivu
			tableRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					MainActivity.this.edit_click(view);
				}
			});

			// Nyt lisää rivi UI:lle uutena rivinä
			this.listaus.addView(tableRow);
		}

	}

	// @Activity
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.getViews();

		this.sdf = new SimpleDateFormat("dd.MM.yyyy");
		this.database = Database.getInstance();

	}
	@Override public void onStart() {
		super.onStart();

		this.userLoginChanged();

		// Jos käyttäjä ei ole kirjautunut, avaa kirjautumisikkuna
		if (FirebaseAuth.getInstance().getCurrentUser() == null) {

			// Avaa kirjautumisikkuna
			Toast.makeText(this, "Kirjautuminen vaaditaan...", Toast.LENGTH_LONG).show();
			this.logInOrOut_click(null);

		} else {
			Toast.makeText(this, "Kirjauduttu sisään käyttäjänä " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
		}

	}
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Jos tähän aktivityyn palattiin tuloksella kirjautumisikkunasta
		if (requestCode == RC_SIGN_IN) {
			onActivityResult_SignIn(resultCode, data);
		}

	}
	protected void onActivityResult_SignIn(int resultCode, Intent data) {

		IdpResponse response = IdpResponse.fromResultIntent(data);

		if (resultCode == RESULT_OK) {

			// Successfully signed in
			Toast.makeText(this, "Kirjautuminen onnistui!", Toast.LENGTH_LONG).show();

			// Päivitä kirjauminen UI:lle
			this.userLoginChanged();

		} else {
			// Sign in failed, check response for error code
			Toast.makeText(this, "Kirjautuminen epäonnistui!", Toast.LENGTH_LONG).show();
			//this.finish();
		}

	}

}
