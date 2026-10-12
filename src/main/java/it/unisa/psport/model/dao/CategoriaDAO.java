package it.unisa.psport.model.dao;

import it.unisa.psport.model.bean.Categoria;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// CategoriaDAO
// tutte le query sulla tabella categoria stanno qui (le servlet non scrivono sql)
// metodi: doRetrieveAll, doRetrieveByKey
public class CategoriaDAO {

    private final DataSource ds;

    // costruttore: il DataSource lo passa chi crea il DAO (listener o servlet)
    public CategoriaDAO(DataSource ds) {
        this.ds = ds;
    }

    // doRetrieveAll
    // cosa fa: legge tutte le categorie in ordine alfabetico
    // output: lista di Categoria (vuota se la tabella e' vuota)
    // usato da: DataSourceListener, per il menu
    // per cambiare l'ordine del menu basta cambiare l'ORDER BY
    public List<Categoria> doRetrieveAll() throws SQLException {
        String sql = "SELECT id, nome, descrizione FROM categoria ORDER BY nome";
        List<Categoria> categorie = new ArrayList<>();

        // try-with-resources: alla fine chiude da solo rs, ps e con
        // chiudere la connessione vuol dire restituirla al pool
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // per ogni riga creo una Categoria e la aggiungo alla lista
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setDescrizione(rs.getString("descrizione"));
                categorie.add(c);
            }
        }
        return categorie;
    }

    // doRetrieveByKey
    // cosa fa: cerca una categoria tramite id
    // input: id della categoria
    // output: la Categoria, oppure null se non esiste
    // usato da: CatalogoServlet, per controllare il parametro ?categoria=
    public Categoria doRetrieveByKey(int id) throws SQLException {
        String sql = "SELECT id, nome, descrizione FROM categoria WHERE id = ?";

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // il valore va al primo ? (gli indici partono da 1), cosi niente sql injection
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                // if e non while: id e' chiave primaria, al massimo c'e' una riga
                if (rs.next()) {
                    Categoria c = new Categoria();
                    c.setId(rs.getInt("id"));
                    c.setNome(rs.getString("nome"));
                    c.setDescrizione(rs.getString("descrizione"));
                    return c;
                }
            }
        }
        return null;
    }
}
