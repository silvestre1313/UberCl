package com.udemy.ubercl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.udemy.ubercl.R;
import com.udemy.ubercl.adapter.RequisicoesAdapter;
import com.udemy.ubercl.config.ConfiguracaoFirebase;
import com.udemy.ubercl.helper.RecyclerItemClickListener;
import com.udemy.ubercl.helper.UsuarioFirebase;
import com.udemy.ubercl.model.Requisicao;
import com.udemy.ubercl.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class RequisicoesActivity extends AppCompatActivity {

    private RecyclerView recyclerRequisicoes;
    private TextView textResultado;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private List<Requisicao> listaRequisiscoes = new ArrayList<>();
    private RequisicoesAdapter adapter;
    private Usuario motorista;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        inicializarComponentes();

        recuperaLocalizacaoUsuario();

    }

    private void recuperaLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                //Recuperar latitude e longitude
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                if (!latitude.isEmpty() && !longitude.isEmpty()){
                    motorista.setLatitude(latitude);
                    motorista.setLongitude(longitude);
                    locationManager.removeUpdates(locationListener);
                    adapter.notifyDataSetChanged();
                }

            }
        };

        //Solicitar atualização de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener
            );
            return;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair:
                autenticacao.signOut();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Requisições");
        setSupportActionBar(toolbar);

        //Configura componentes
        recyclerRequisicoes = findViewById(R.id.recyclerRequisicoes);
        textResultado = findViewById(R.id.textResultado);

        //Configurações inicias
        motorista = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Configurar recycler view
        adapter = new RequisicoesAdapter(getApplicationContext(), listaRequisiscoes, motorista);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerRequisicoes.setLayoutManager(layoutManager);
        recyclerRequisicoes.setHasFixedSize(true);
        recyclerRequisicoes.setAdapter(adapter);

        //Adicionar evento de clique
        recyclerRequisicoes.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerRequisicoes,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Requisicao requisicao = listaRequisiscoes.get(position);
                        Intent i = new Intent(RequisicoesActivity.this, CorridaActivity.class);
                        i.putExtra("idRequisicao", requisicao.getId());
                        i.putExtra("motorista", motorista);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        recuperarRequisicoes();

    }

    private void recuperarRequisicoes(){

        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild("status")
                .equalTo(Requisicao.STATUS_AGUARDANDO);
        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() > 0){
                    textResultado.setVisibility(View.GONE);
                    recyclerRequisicoes.setVisibility(View.VISIBLE);
                } else{
                    textResultado.setVisibility(View.VISIBLE);
                    recyclerRequisicoes.setVisibility(View.GONE);
                }

                for (DataSnapshot ds: snapshot.getChildren()){
                    Requisicao requisicao = ds.getValue(Requisicao.class);
                    listaRequisiscoes.add(requisicao);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}