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
import java.sql.SQLException;
import java.util.List;

// RicercaServlet (controller della ricerca)
// url: /ricerca?q=testo  -> prodotti attivi il cui nome, marca o descrizione contengono il testo
// parametri: q (testo da cercare; se manca o e' vuoto si torna al catalogo completo)
// mette in request: "prodotti" (List<Prodotto>), "titolo" (String),
//                   "ricerca" (String, il testo cercato: catalogo.jsp lo usa per breadcrumb e messaggio "nessun risultato")
// vista: /WEB-INF/view/catalogo.jsp (la stessa del catalogo, cosi le card non si duplicano)
// chiamata da: il form della barra di ricerca in header.jsp
@WebServlet("/ricerca")
public class RicercaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // parametro dall'url: String, null se manca
        String q = request.getParameter("q");

        // ricerca vuota (es. click su "Cerca" con la casella vuota): non ha senso cercare,
        // mando l'utente al catalogo completo. Il return serve per non fare il forward dopo il redirect
        if (q == null || q.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }
        q = q.trim();

        // DataSource preso dal context (lo mette il DataSourceListener) e DAO che serve
        DataSource ds = (DataSource) getServletContext().getAttribute("DataSource");
        ProdottoDAO prodottoDAO = new ProdottoDAO(ds);

        try {
            List<Prodotto> prodotti = prodottoDAO.doRetrieveByRicerca(q);

            request.setAttribute("prodotti", prodotti);
            request.setAttribute("titolo", "Risultati per \"" + q + "\"");
            // il testo cercato: la jsp lo stampa con c:out, quindi niente rischio XSS
            request.setAttribute("ricerca", q);

        } catch (SQLException e) {
            throw new ServletException("Errore nella ricerca dei prodotti", e);
        }

        // forward alla vista del catalogo
        request.getRequestDispatcher("/WEB-INF/view/catalogo.jsp").forward(request, response);
    }
}
