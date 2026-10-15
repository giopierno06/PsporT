package it.unisa.psport.controller;

import it.unisa.psport.model.bean.Prodotto;
import it.unisa.psport.model.dao.ProdottoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

// SuggerimentiServlet (risposta AJAX in JSON, nessuna vista jsp)
// url: /ricerca/suggerimenti?q=testo  -> al massimo 5 prodotti che corrispondono al testo
// parametri: q (testo digitato; sotto i 2 caratteri risponde con lista vuota)
// risposta: application/json, array di oggetti
//           [{"id":1,"nome":"...","marca":"...","prezzo":19.90}, ...]
// chiamata da: js/ricerca.js (suggerimenti sotto la barra di ricerca dell'header)
@WebServlet("/ricerca/suggerimenti")
public class SuggerimentiServlet extends HttpServlet {

    private static final int MAX_SUGGERIMENTI = 5;
    private static final int MIN_CARATTERI = 2;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // il tipo e la codifica vanno impostati prima di prendere il writer
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String q = request.getParameter("q");
        PrintWriter out = response.getWriter();

        // testo mancante o troppo corto: lista vuota, non ha senso interrogare il db
        if (q == null || q.trim().length() < MIN_CARATTERI) {
            out.print("[]");
            return;
        }

        DataSource ds = (DataSource) getServletContext().getAttribute("DataSource");
        ProdottoDAO prodottoDAO = new ProdottoDAO(ds);

        try {
            List<Prodotto> trovati = prodottoDAO.doRetrieveByRicerca(q.trim());
            List<Prodotto> primi = trovati.subList(0, Math.min(trovati.size(), MAX_SUGGERIMENTI));

            // il json lo costruisco a mano (il progetto non usa librerie json):
            // le stringhe passano da escape() per virgolette, backslash e caratteri di controllo
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < primi.size(); i++) {
                Prodotto p = primi.get(i);
                if (i > 0) {
                    json.append(',');
                }
                json.append("{\"id\":").append(p.getId())
                    .append(",\"nome\":\"").append(escape(p.getNome())).append('"')
                    .append(",\"marca\":\"").append(escape(p.getMarca())).append('"')
                    .append(",\"prezzo\":").append(p.getPrezzo().toPlainString())
                    .append('}');
            }
            json.append(']');
            out.print(json);

        } catch (SQLException e) {
            throw new ServletException("Errore nei suggerimenti di ricerca", e);
        }
    }

    // escape (metodo di supporto)
    // rende una stringa sicura da mettere tra virgolette in un json; null diventa stringa vuota
    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}
