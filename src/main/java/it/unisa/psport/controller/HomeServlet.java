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

// HomeServlet (controller della home)
// url: "" (radice del sito, localhost:8080/PsporT/) e "/home"
// cosa fa: prende dal db gli ultimi prodotti inseriti e li passa a home.jsp
// mette in request: "novita" (List<Prodotto>)
// vista: /WEB-INF/view/home.jsp
// nota per me: non c'e' index.jsp perche' la home deve passare dalla servlet per avere i dati
@WebServlet(urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    // numero di prodotti nella sezione Novita
    // per mostrarne di piu' o di meno cambiare solo questo numero
    private static final int NUMERO_NOVITA = 8;

    // doGet: la home si apre solo con un link o scrivendo l'url, quindi basta il GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. recupero il DataSource che il DataSourceListener ha messo nel ServletContext all'avvio
        DataSource ds = (DataSource) getServletContext().getAttribute("DataSource");
        ProdottoDAO prodottoDAO = new ProdottoDAO(ds);

        // 2. chiedo al DAO le novita e le salvo in request (nella jsp le leggo con ${novita})
        // se il db da errore rilancio una ServletException, Tomcat mostra la pagina di errore
        try {
            List<Prodotto> novita = prodottoDAO.doRetrieveNovita(NUMERO_NOVITA);
            request.setAttribute("novita", novita);
        } catch (SQLException e) {
            throw new ServletException("Errore nel caricamento dei prodotti", e);
        }

        // 3. forward alla jsp: stesso request quindi la jsp vede gli attributi
        // la jsp sta in WEB-INF, dal browser non si puo' aprire direttamente
        request.getRequestDispatcher("/WEB-INF/view/home.jsp").forward(request, response);
    }
}
