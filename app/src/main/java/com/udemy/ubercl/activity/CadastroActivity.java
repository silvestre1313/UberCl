package com.udemy.ubercl.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.udemy.ubercl.R;
import com.udemy.ubercl.config.ConfiguracaoFirebase;
import com.udemy.ubercl.helper.UsuarioFirebase;
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
        ).addOnCompleteListener(this, task -> {

            if (task.isSuccessful()){

                try {

                    String idUsuario = task.getResult().getUser().getUid();
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();

                    //Atualizar nome no userProfile
                    UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                    if (verificaTipoUsuario() == "p"){
                        startActivity(new Intent(CadastroActivity.this, PassageiroActivity.class));
                        finish();

                        Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar passageiro", Toast.LENGTH_SHORT).show();

                    }else{
                        startActivity(new Intent(CadastroActivity.this, RequisicoesActivity.class));
                        finish();

                        Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar motorista", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }

            } else{
                String excecao = "";
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e){
                    excecao = "Digite uma senha mais forte!";
                } catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Por favor, digite um email valido";
                } catch (FirebaseAuthUserCollisionException e){
                    excecao = "Esta conta ja foi cadastrada";
                } catch (Exception e){
                    excecao = "Erro ao cadastrar usuario: " + e.getMessage();
                    e.printStackTrace();
                }
            }

        });

    }

    public String verificaTipoUsuario(){

        return switchTipoUsuario.isChecked() ? "M" : "P" ;

    }

}