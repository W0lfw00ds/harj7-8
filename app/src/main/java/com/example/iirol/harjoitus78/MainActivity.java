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

import com.example.iirol.harjoitus78.Database.Firebase.FirebaseDatabase;
import com.example.iirol.harjoitus78.Database.DatabaseException;
import com.example.iirol.harjoitus78.Database.Firebase.FirebaseRepository;
import com.example.iirol.harjoitus78.Database.Entities.Kirja;
import com.example.iirol.harjoitus78.Database.Firebase.Repositories.KirjaFirebaseRepository;
import com.example.iirol.harjoitus78.Database.Repository;
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

	// CONSTANTS
	private static final int RC_SIGN_IN = 123;

	// FIELDS
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
	private FirebaseDatabase firebaseDatabase;

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

		// Luo uusi intent millä siirrytään activitystä toiseen, ja lisää bundle siihen mukaan
		Intent intent = new Intent(this, EditKirja.class);
		intent.putExtra(Kirja.class.getSimpleName(), editableKirja);

		// Siirry toiseen activityyn
		this.startActivity(intent);
	}
	private void addnew_click(final View view) {

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

		// Disabloi nappula
		view.setEnabled(false);

		// Luo uusi kirja
		Kirja uusiKirja = new Kirja(parsedNumero, parsedNimi, parsedPainos, parsedStringHankintapvm);
		this.firebaseDatabase.KirjaRepository.add(uusiKirja, new FirebaseRepository.ResultListener() {

			@Override public void onSuccess() {
				Toast.makeText(getApplicationContext(),"Uusi kirja lisätty!", Toast.LENGTH_LONG).show();
				MainActivity.this.listaaKirjat();
				view.setEnabled(true);
			}

			@Override public void onError(DatabaseException repositoryException) {
				Toast.makeText(getApplicationContext(),"Jotakin meni pieleen uuden luonnissa...", Toast.LENGTH_LONG).show();
				view.setEnabled(true);
			}

		});

	}
	private void deletefirst_click(final View view) {

		// Disabloi nappula
		view.setEnabled(false);

		// Poista ensimmäinen rivi tietokannasta
		this.firebaseDatabase.KirjaRepository.deleteFirst(
			new FirebaseRepository.ResultListener() {

				@Override public void onSuccess() {
	                  Toast.makeText(getApplicationContext(),"Ensimmäinen kirja poistettiin!", Toast.LENGTH_LONG).show();
                      MainActivity.this.listaaKirjat();
                      view.setEnabled(true);
                  }

                  @Override public void onError(DatabaseException repositoryException) {
	                  Toast.makeText(getApplicationContext(),"Kirjoja ei ole!", Toast.LENGTH_LONG).show();
                      view.setEnabled(true);
                  }

              }
		);



	}
	private void logInOrOut_click(final View view) {

		if (FirebaseAuth.getInstance().getCurrentUser() != null) {

			view.setEnabled(false);
			AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
				public void onComplete(@NonNull Task<Void> task) {
					view.setEnabled(true);
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
		this.firebaseDatabase.KirjaRepository.getAll(new FirebaseRepository.ResultItemsListener<Kirja>() {

			@Override public void onSuccess(ArrayList<Kirja> kirjat) {

					// Käy jokainen löytynyt kirja läpi
					for (Kirja kirja : kirjat) {

						// Luo kirjalle oma teksti-elementtie UI:lle
						TextView textView = new TextView(MainActivity.this);
						textView.setText(kirja.toString());
						textView.setTextSize(17f);

						// Lisää rivi jonka sisälle teksti laitetaan
						TableRow tableRow = new TableRow(MainActivity.this);
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
						MainActivity.this.listaus.addView(tableRow);
					}
			}

			@Override public void onError(DatabaseException repositoryException) {
				Toast.makeText(MainActivity.this, "Kirjoja ei jostakin syystä saatu!", Toast.LENGTH_LONG).show();
			}

		});

	}

	// @Activity
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.getViews();

		this.sdf = new SimpleDateFormat("dd.MM.yyyy");
		this.firebaseDatabase = FirebaseDatabase.getInstance();
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

		harjoitus78.getSQLiteDatabase().KirjaRepository.add(new Kirja(2, "toka", 2, "25.01.2017"));
		harjoitus78.getSQLiteDatabase().KirjaRepository.getAll(new Repository.ResultItemsListener<Kirja>() {

			@Override public void onSuccess(ArrayList<Kirja> entities) {
				for (Kirja kirja : entities) {
					System.out.print(kirja.toString());
				}
			}

			@Override public void onError(DatabaseException databaseException) {
				System.out.print(databaseException.toString());
			}

		});

	}
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Jos tähän aktivityyn palattiin tuloksella kirjautumisikkunasta
		if (requestCode == RC_SIGN_IN) {
			onActivityResult_SignIn(resultCode, data);
		}

	}
	protected void onActivityResult_SignIn(int resultCode, Intent data) {

		//IdpResponse response = IdpResponse.fromResultIntent(data);

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
