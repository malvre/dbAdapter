package com.procergs.apm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.procergs.apm.utils.Dialogs;
import com.procergs.apm.utils.ErrorHandler;
import com.procergs.apm.utils.Validators;

/******************************************************************************************************
 * Classe para manipular o banco SQLite do Android
 * 
 * @author Marcelo Rezende
 */
public class DBAdapter {
	private static final String	DATABASE_NAME		= "apm";
	private static final int	DATABASE_VERSION	= 6;
	private final Context		context;
	private DatabaseHelper		dbhelper;
	private SQLiteDatabase		db;

	/**************************************************************************************************
	 * Construtor
	 * 
	 * @param ctx
	 */
	public DBAdapter(Context ctx) {
		this.context = ctx;
		dbhelper = new DatabaseHelper(context);
		open();
	}

	/**************************************************************************************************
	 * Abre a conexão
	 * 
	 * @return
	 */
	private void open() throws SQLException {
		if (db == null) {
			db = dbhelper.getWritableDatabase();
		}
	}

	/**************************************************************************************************
	 * Fecha a conexão
	 */
	public void close() {
		if (db != null) {
			dbhelper.close();
			db = null;
		}
	}

	/**************************************************************************************************
	 * innerclass para criação do banco e retorno da conexão
	 * 
	 * @author marcelo
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_ALUNO);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNO);

			onCreate(db);

		}
	}

	/***************************************************************************************************
	 * Operações na tabela ALUNO
	 */
	public static final String		TABLE_ALUNO				= "aluno";
	public static final String		COL_ALUNO_NOME			= "nome";
	public static final String		COL_ALUNO_NASCIMENTO	= "dt_nascimento";
	public static final String		COL_ALUNO_TELEFONE		= "telefone";
	public static final String		COL_ALUNO_EMAIL			= "email";
	public static final String		COL_ALUNO_OBSERVACAO	= "observacao";

	private static final String[]	COLS_ALUNO				= new String[] {
			BaseColumns._ID,
			COL_ALUNO_NOME,
			COL_ALUNO_NASCIMENTO,
			COL_ALUNO_EMAIL,
			COL_ALUNO_TELEFONE,
			COL_ALUNO_OBSERVACAO							};

	public static final String		CREATE_TABLE_ALUNO		= "CREATE TABLE " + TABLE_ALUNO + "(" + BaseColumns._ID
																	+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_ALUNO_NOME + " TEXT,"
																	+ COL_ALUNO_NASCIMENTO + " TEXT, " + COL_ALUNO_TELEFONE + " TEXT,"
																	+ COL_ALUNO_EMAIL + " TEXT," + COL_ALUNO_OBSERVACAO + " TEXT" + ")";

	/***
	 * incluir aluno
	 */
	public long insertAluno(String nome, String nascimento, String email, String telefone, String observacao) throws Exception {
		ContentValues values = new ContentValues();
		values.put(COL_ALUNO_NOME, nome);
		values.put(COL_ALUNO_NASCIMENTO, nascimento);
		values.put(COL_ALUNO_EMAIL, email);
		values.put(COL_ALUNO_TELEFONE, telefone);
		values.put(COL_ALUNO_OBSERVACAO, observacao);

		validateAluno(values);
		return db.insert(TABLE_ALUNO, null, values);
	}

	/***
	 * atualiza aluno
	 */
	public boolean updateAluno(long id, String nome, String nascimento, String email, String telefone, String observacao) throws Exception {
		ContentValues values = new ContentValues();
		values.put(COL_ALUNO_NOME, nome);
		values.put(COL_ALUNO_NASCIMENTO, nascimento);
		values.put(COL_ALUNO_EMAIL, email);
		values.put(COL_ALUNO_TELEFONE, telefone);
		values.put(COL_ALUNO_OBSERVACAO, observacao);

		validateAluno(values);
		return db.update(TABLE_ALUNO, values, BaseColumns._ID + "=?", new String[] { String.valueOf(id) }) > 0;
	}

	/***
	 * excluir aluno
	 */
	public boolean deleteAluno(long id) {
		return db.delete(TABLE_ALUNO, BaseColumns._ID + "=?", new String[] { String.valueOf(id) }) > 0;
	}

	/***
	 * lista todos os alunos
	 */
	public Cursor getAllAlunos(String s) {
		return db.query(TABLE_ALUNO, COLS_ALUNO, COL_ALUNO_NOME + " LIKE ?", new String[] { "%" + s + "%" }, null, null, COL_ALUNO_NOME
				+ " COLLATE NOCASE ASC");
	}

	/***
	 * consulta aluno
	 */
	public Cursor getAluno(long id) {
		Cursor c = db.query(TABLE_ALUNO, COLS_ALUNO, BaseColumns._ID + "=?", new String[] { String.valueOf(id) }, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	/***
	 * valida o content values de aluno
	 */
	public void validateAluno(ContentValues values) throws Exception {
		ErrorHandler erro = new ErrorHandler();
		if (values.getAsString(COL_ALUNO_NOME).length() == 0) {
			erro.add("Nome do aluno deve ser informado");
		}
		if (values.getAsString(COL_ALUNO_EMAIL).length() == 0) {
			erro.add("E-mail deve ser informado");
		}
		if (!Validators.date(values.getAsString(COL_ALUNO_NASCIMENTO), "yyyy-MM-dd")) {
			erro.add("Data de nascimento deve ser válida");
		}
		if (erro.hasError()) {
			throw new Exception(erro.getMessage());
		}
	}

	/***
	 * criação de registros de teste
	 */
	public void criaRegistrosDeTeste() {
		try {
			insertAluno("João da Silva", "1969-05-23", "joao.silva@gmail.com", "95452344", "Bla bla bla");
			insertAluno("Alex Krueger Muller", "1969-05-23", "alex.krueger@gmail.com", "95452344", "Bla bla bla");
			insertAluno("Cristiane da Silva", "1969-05-23", "cristiane.silva@gmail.com", "95452344", "Bla bla bla");
			insertAluno("Rasmus Lerdof", "1969-05-23", "rasmus@php.net", "95452344", "Bla bla bla");
			insertAluno("Morpheus", "1969-05-23", "morpheus@zion.com", "95452344", "Bla bla bla");
			insertAluno("Mickey Mouse", "1969-05-23", "mickey@disney.com", "95452344", "Bla bla bla");
			insertAluno("Quentin Tarantino", "1969-05-23", "tarantino@gmail.com", "95452344", "Bla bla bla");
			insertAluno("Steve Jobs", "1969-05-23", "steve@apple.com", "95452344", "Bla bla bla");
			insertAluno("James Tiberius Kirk", "1969-05-23", "kirk@enterprise.com", "95452344", "Bla bla bla");
			insertAluno("Padme Amidala", "1969-05-23", "padme@gmail.com", "95452344", "Bla bla bla");
		} catch (Exception ex) {
			Dialogs.toast(context, "Erro ao criar registros de teste");
		}
	}
}
