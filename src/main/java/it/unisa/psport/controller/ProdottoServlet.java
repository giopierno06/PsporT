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

// ProdottoServlet (controller della pagina di dettaglio di un prodotto)
// url: /prodotto?id=N  -> mostra il prodotto con id N
// parametri: id (obbligatorio, intero)
// mette in request: "prodotto" (Prodotto)
// errori: 404 se id manca o non e' un numero, o se il prodotto non esiste (o non e' attivo)
// vista: /WEB-INF/view/prodotto.jsp
// il link arriva da cardProdotto.jspf
@WebServlet("/prodotto")
public class ProdottoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // DataSource preso dal context (lo mette il DataSourceListener) e DAO che serve
        DataSource ds = (DataSource) getServletContext().getAttribute("DataSource");
        ProdottoDAO prodottoDAO = new ProdottoDAO(ds);

        // parametro dall'url: e' sempre una String, null se manca
        // va controllato perche' l'utente nell'url puo' scrivere qualsiasi cosa
        String paramId = request.getParameter("id");

        // converto l'id in int
        // se manca (null) o non e' un numero (es. ?id=abc) parseInt lancia NumberFormatException
        int id;
        try {
            id = Integer.parseInt(paramId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Prodotto non valido");
            return;   // il return serve, senza si arriva al forward dopo sendError e da errore
        }

        try {
            // cerco il prodotto nel db: il DAO restituisce null se non esiste o non e' attivo
            Prodotto prodotto = prodottoDAO.doRetrieveById(id);
            if (prodotto == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Prodotto non trovato");
                return;
            }

            // il prodotto in request serve a prodotto.jsp (titolo della pagina e dettagli)
            request.setAttribute("prodotto", prodotto);

        } catch (SQLException e) {
            throw new ServletException("Errore nel caricamento del prodotto", e);
        }

        // forward alla vista del prodotto
        request.getRequestDispatcher("/WEB-INF/view/prodotto.jsp").forward(request, response);
    }
}
