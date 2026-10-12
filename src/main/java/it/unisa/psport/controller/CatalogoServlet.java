package it.unisa.psport.controller;

import it.unisa.psport.model.bean.Categoria;
import it.unisa.psport.model.bean.Prodotto;
import it.unisa.psport.model.dao.CategoriaDAO;
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

// CatalogoServlet (controller del catalogo)
// url: /catalogo              -> tutti i prodotti attivi
//      /catalogo?categoria=N  -> solo i prodotti della categoria con id N
// parametri: categoria (opzionale, intero)
// mette in request: "prodotti" (List<Prodotto>), "titolo" (String),
//                   "categoria" (Categoria, solo se c'e' il filtro)
// errori: 404 se il parametro non e' un numero o la categoria non esiste
// vista: /WEB-INF/view/catalogo.jsp
@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // DataSource preso dal context (lo mette il DataSourceListener) e DAO che servono
        DataSource ds = (DataSource) getServletContext().getAttribute("DataSource");
        ProdottoDAO prodottoDAO = new ProdottoDAO(ds);
        CategoriaDAO categoriaDAO = new CategoriaDAO(ds);

        // parametro dall'url: e' sempre una String, null se manca
        // va controllato perche' l'utente nell'url puo' scrivere qualsiasi cosa
        String paramCategoria = request.getParameter("categoria");

        try {
            List<Prodotto> prodotti;
            String titolo;

            if (paramCategoria == null || paramCategoria.isBlank()) {
                // caso 1: nessun filtro, prendo tutto
                prodotti = prodottoDAO.doRetrieveAll();
                titolo = "Tutti i prodotti";

            } else {
                // caso 2: filtro per categoria
                // prima converto il parametro in int, se non e' un numero (es. ?categoria=abc) do 404
                int idCategoria;
                try {
                    idCategoria = Integer.parseInt(paramCategoria);
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Categoria non valida");
                    return;   // il return serve, senza si arriva al forward dopo sendError e da errore
                }

                // poi controllo che la categoria esista davvero (es. ?categoria=999 -> 404)
                Categoria categoria = categoriaDAO.doRetrieveByKey(idCategoria);
                if (categoria == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Categoria non trovata");
                    return;
                }

                prodotti = prodottoDAO.doRetrieveByCategoria(idCategoria);
                titolo = categoria.getNome();
                // la categoria in request serve a catalogo.jsp (breadcrumb e descrizione)
                // e a header.jsp (evidenzia la voce del menu)
                request.setAttribute("categoria", categoria);
            }

            request.setAttribute("prodotti", prodotti);
            request.setAttribute("titolo", titolo);

        } catch (SQLException e) {
            throw new ServletException("Errore nel caricamento del catalogo", e);
        }

        // forward alla vista del catalogo
        request.getRequestDispatcher("/WEB-INF/view/catalogo.jsp").forward(request, response);
    }
}
