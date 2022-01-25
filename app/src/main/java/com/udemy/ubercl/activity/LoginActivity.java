package com.udemy.ubercl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.udemy.ubercl.R;
import com.udemy.ubercl.config.ConfiguracaoFirebase;
import com.udemy.ubercl.helper.UsuarioFirebase;
import com.udemy.ubercl.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializar componentes
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);

    }

    public void validarLoginUsuario(View view){

        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();

        if (!email.isEmpty()){
            if (!senha.isEmpty()){

                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setSenha(senha);

                logarUsuario(usuario);

            }else{
                Toast.makeText(LoginActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(LoginActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
        }

    }

    public void logarUsuario(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()){

                UsuarioFirebase.redirecionaUsuarioLogado(LoginActivity.this);

                startActivity(new Intent());

            }else{

                String excecao = "";
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidUserException e){
                    excecao = "Usuario nao esta cadastrado";
                } catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Email e senha nao correspondem a um cadastrado";
                } catch (Exception e){
                    excecao = "Erro ao cadastrar usuario: " + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
            }
        });

    }

}