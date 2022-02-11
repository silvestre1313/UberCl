package com.udemy.ubercl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.udemy.ubercl.R;
import com.udemy.ubercl.config.ConfiguracaoFirebase;
import com.udemy.ubercl.model.Requisicao;

import java.util.ArrayList;
import java.util.List;

public class RequisicoesActivity extends AppCompatActivity {

    private RecyclerView recyclerRequisicoes;
    private TextView textResultado;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private List<Requisicao> listaRequisiscoes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        inicializarComponentes();

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
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}