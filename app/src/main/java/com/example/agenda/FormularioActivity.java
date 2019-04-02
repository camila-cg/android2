package com.example.agenda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.agenda.dao.PessoaDAO;
import com.example.agenda.modelo.Pessoa;

import java.io.File;

public class FormularioActivity extends AppCompatActivity {
    private FormularioHelper helper;

    //TODO: Salvar dados preenchidos independente de mudança de orientação da tela.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        helper = new FormularioHelper(this);

        final Intent intent = getIntent();
        Pessoa pessoa = (Pessoa) intent.getSerializableExtra("pessoa");
        if(pessoa != null){
            //Abro o formulário com os dados de um registro existente para fins de edição.
            helper.preencheFormulario(pessoa);
        }

        //Botão de tirar foto para cadastro
        //cada aplicação só pode escrever na área q foi instalada
        Button btnFoto = findViewById(R.id.btn_camera);
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String caminhoFoto =  getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                File arquivoFoto = new File(caminhoFoto);
                Uri fotoUri = FileProvider.getUriForFile( getApplicationContext(), getApplicationContext().getPackageName() + ".provider", arquivoFoto);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivity(intentCamera);

            }
        });

    }

    //Colocando o botão de salvar formulário na action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //Colocando ação no botão do menu da action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Restringindo o finish() apenas quando a opção do menu clicado for para salvar formulário
        switch (item.getItemId()){
            case R.id.menu_formulario:
                Pessoa pessoa = helper.obterPessoa();
                PessoaDAO dao = new PessoaDAO(this);

                //verificar se é um cadastro novo ou uma atualização
                if(pessoa.getId() == null){
                    dao.incluir(pessoa);
                }else{
                    dao.editar(pessoa);
                }

                dao.close();
                Toast.makeText(FormularioActivity.this,"Pessoa " + pessoa.getNome() + " cadastrada!", Toast.LENGTH_SHORT).show();

                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
