package com.example.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.agenda.modelo.Pessoa;

import java.util.ArrayList;
import java.util.List;

public class PessoaDAO extends SQLiteOpenHelper {

    /**
     * Construtor obrigatório
     * @param context contexto da conexão //TODO: MELHORAR DESCRIÇÃO
     */
    public PessoaDAO( Context context) {
        //TODO: PESQUISAR O PAPEL DA FACTORY NA CONEXÃO COM BANCO
        super(context, "AgendaDB", null, 2);
    }

    /**
     * Método chamado quando o banco de dados é criado.
     * Após a criação criamos a tabela que será usada para persistência do objeto Pessoa.
     * @param db instância do banco de dados criado.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Pessoa (id INTEGER PRIMARY KEY," +
                "nome TEXT NOT NULL," +
                "endereco TEXT," +
                "telefone TEXT," +
                "site TEXT," +
                "caminhoFoto TEXT," +
                "nota REAL);";

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: VALIDAR SE É A MELHOR FORMA PARA ATUALIZAR BANCO DE USUÁRIOS --> MIGRATIONS
        String sql = "";
        switch (oldVersion){
            case 1: sql = "ALTER TABLE Pessoa ADD COLUMN caminhoFoto TEXT";
                    db.execSQL(sql);
        }

    }


    public void incluir(Pessoa pessoa) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = obterDadosPessoa(pessoa);

        db.insert("Pessoa", null, dados);

    }

    private ContentValues obterDadosPessoa(Pessoa pessoa) {
        ContentValues dados = new ContentValues(); //funciona como um map do java (chave e valor)

        dados.put("nome", pessoa.getNome());
        dados.put("endereco", pessoa.getEndereco());
        dados.put("telefone", pessoa.getTelefone());
        dados.put("site", pessoa.getSite());
        dados.put("nota", pessoa.getNota());
        dados.put("caminhoFoto", pessoa.getCaminhoFoto());
        return dados;
    }

    /** Efetua a busca por Pessoas no banco.
     * Como o resultado da execução da query não retorna uma lista de objetos do tipo Pessoa, precisamos manipular o resultado com cursor.
     * O cursor aponta para um linha em branco exatamente acima dos resultados, por isso é importante usar o moveToNext() para mover o cursor e validar se há mais registros.
     * @return lista de pessoas cadastradas.
     */
    public List<Pessoa> pesquisar() {
        String sql = "SELECT * FROM Pessoa;";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        List<Pessoa> pessoas = new ArrayList<Pessoa>();
        while(cursor.moveToNext()){
            Pessoa pessoa = new Pessoa();
            pessoa.setId(cursor.getLong(cursor.getColumnIndex("id")));
            pessoa.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            pessoa.setEndereco(cursor.getString(cursor.getColumnIndex("endereco")));
            pessoa.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
            pessoa.setSite(cursor.getString(cursor.getColumnIndex("site")));
            pessoa.setNota(cursor.getDouble(cursor.getColumnIndex("nota")));
            pessoa.setCaminhoFoto(cursor.getString(cursor.getColumnIndex("caminhoFoto")));

            pessoas.add(pessoa);
        }
        cursor.close();

        return pessoas;
    }

    public void remover(Pessoa pessoa) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = { pessoa.getId().toString()};

        db.delete("Pessoa","id = ?", params);
    }

    public void editar(Pessoa pessoa) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = obterDadosPessoa(pessoa);
        String[] params = { pessoa.getId().toString()};

        db.update("Pessoa", dados, "id = ?", params );
    }
}
