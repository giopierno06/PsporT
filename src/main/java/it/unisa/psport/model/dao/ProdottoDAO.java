package it.unisa.psport.model.dao;

import it.unisa.psport.model.bean.Prodotto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// ProdottoDAO
// tutte le query sulla tabella prodotto stanno qui
// metodi: doRetrieveNovita, doRetrieveAll, doRetrieveByCategoria, doRetrieveByRicerca
// regola: tutte le query prendono solo i prodotti con attivo = TRUE
// (un prodotto tolto dalla vendita non si cancella, si mette attivo = FALSE
// perche' gli ordini vecchi lo usano ancora)
public class ProdottoDAO {

    private final DataSource ds;

    // costruttore: il DataSource lo passa la servlet
    public ProdottoDAO(DataSource ds) {
        this.ds = ds;
    }

    // doRetrieveNovita
    // cosa fa: prende gli ultimi prodotti inseriti
    // input: limite = quanti prodotti al massimo
    // output: lista di Prodotto dal piu' recente
    // usato da: HomeServlet (sezione Novita)
    // ordino anche per id perche' molti prodotti hanno la stessa data
    public List<Prodotto> doRetrieveNovita(int limite) throws SQLException {
        String sql = "SELECT * FROM prodotto WHERE attivo = TRUE "
                   + "ORDER BY data_inserimento DESC, id DESC LIMIT ?";
        List<Prodotto> prodotti = new ArrayList<>();

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // valore passato con setInt e non concatenato alla stringa: niente sql injection
            ps.setInt(1, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(creaProdotto(rs));
                }
            }
        }
        return prodotti;
    }

    // doRetrieveAll
    // cosa fa: prende tutti i prodotti attivi in ordine di nome
    // output: lista di Prodotto
    // usato da: CatalogoServlet senza filtro (/catalogo)
    // per ordinare per prezzo: ORDER BY prezzo (DESC per il contrario)
    public List<Prodotto> doRetrieveAll() throws SQLException {
        String sql = "SELECT * FROM prodotto WHERE attivo = TRUE ORDER BY nome";
        List<Prodotto> prodotti = new ArrayList<>();

        // qui il ResultSet sta nello stesso try perche' non ci sono parametri da settare prima
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prodotti.add(creaProdotto(rs));
            }
        }
        return prodotti;
    }

    // doRetrieveByCategoria
    // cosa fa: prende i prodotti attivi di una sola categoria
    // input: idCategoria
    // output: lista di Prodotto (vuota se la categoria non ha prodotti)
    // usato da: CatalogoServlet con filtro (/catalogo?categoria=N)
    // per filtrare anche per tipo: aggiungere "AND id_tipo = ?" e un altro setInt(2, ...)
    public List<Prodotto> doRetrieveByCategoria(int idCategoria) throws SQLException {
        String sql = "SELECT * FROM prodotto WHERE attivo = TRUE AND id_categoria = ? ORDER BY nome";
        List<Prodotto> prodotti = new ArrayList<>();

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(creaProdotto(rs));
                }
            }
        }
        return prodotti;
    }

    // creaProdotto (metodo di supporto, private)
    // cosa fa: trasforma la riga corrente del ResultSet in un oggetto Prodotto
    // sta a parte per non ripetere gli stessi setter in ogni metodo
    // se aggiungo una colonna alla tabella devo aggiungerla qui e nel bean
    private Prodotto creaProdotto(ResultSet rs) throws SQLException {
        Prodotto p = new Prodotto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setMarca(rs.getString("marca"));
        p.setDescrizione(rs.getString("descrizione"));
        p.setPrezzo(rs.getBigDecimal("prezzo"));
        p.setIva(rs.getBigDecimal("iva"));
        p.setImmagine(rs.getString("immagine"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        p.setIdTipo(rs.getInt("id_tipo"));
        p.setAttivo(rs.getBoolean("attivo"));
        // Timestamp di sql convertito in LocalDateTime di java
        p.setDataInserimento(rs.getTimestamp("data_inserimento").toLocalDateTime());
        return p;
    }

    // doRetrieveByRicerca
    // cosa fa: cerca i prodotti attivi che contengono il testo nel nome, nella marca o nella descrizione
    // input: testo = cio' che ha scritto l'utente (gia' senza spazi ai lati)
    // output: lista di Prodotto in ordine di nome (vuota se non trova niente)
    // usato da: RicercaServlet (/ricerca?q=testo)
    // LIKE non distingue maiuscole/minuscole con la collation di default di MySQL
    public List<Prodotto> doRetrieveByRicerca(String testo) throws SQLException {
        // ESCAPE '\\' serve per cercare % e _ come caratteri normali (vedi sotto)
        String sql = "SELECT * FROM prodotto "
                   + "WHERE attivo = TRUE AND (nome LIKE ? ESCAPE '\\\\' OR marca LIKE ? ESCAPE '\\\\' "
                   + "OR descrizione LIKE ? ESCAPE '\\\\') "
                   + "ORDER BY nome";
        List<Prodotto> prodotti = new ArrayList<>();

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // in LIKE % e _ sono jolly: se l'utente scrive "100%" non deve trovare tutto,
            // quindi li faccio precedere da \ (prima il \ stesso, poi gli altri due)
            String pulito = testo.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
            // i % ai lati significano "contiene": il testo puo' stare in qualsiasi punto
            // il pattern e' passato con setString, non concatenato nella query: niente sql injection
            String pattern = "%" + pulito + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(creaProdotto(rs));
                }
            }
        }
        return prodotti;
    }

}


