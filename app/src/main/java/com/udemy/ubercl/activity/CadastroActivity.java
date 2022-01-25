package com.udemy.ubercl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.udemy.ubercl.R;
import com.udemy.ubercl.config.ConfiguracaoFirebase;
import com.udemy.ubercl.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private Switch switchTipoUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //Inicializar componentes
        campoNome = findViewById(R.id.editCadastroNome);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        switchTipoUsuario = findViewById(R.id.switchTipoUsuario);

    }

    public void validarCadastroUsuario(View view){

        //Recuperar textos dos campos
        String nome = campoNome.getText().toString();
        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();

        if (!nome.isEmpty()){
            if (!email.isEmpty()){
                if (!senha.isEmpty()){

                    Usuario usuario = new Usuario();
                    usuario.setNome(nome);
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    usuario.setTipo(verificaTipoUsuario());

                    cadastrarUsuario(usuario);

                }else{
                    Toast.makeText(CadastroActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(CadastroActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(CadastroActivity.this, "Preencha o nome", Toast.LENGTH_SHORT).show();
        }

    }

    public void cadastrarUsuario(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuario", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public String verificaTipoUsuario(){

        return switchTipoUsuario.isChecked() ? "M" : "P" ;

    }

}