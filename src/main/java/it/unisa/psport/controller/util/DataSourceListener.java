package it.unisa.psport.controller.util;

import it.unisa.psport.model.dao.CategoriaDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

// DataSourceListener (ServletContextListener)
// quando parte: una volta sola, all'avvio dell'applicazione su Tomcat
// cosa fa:
//   1. prende con JNDI il pool di connessioni definito in META-INF/context.xml
//   2. lo salva nel ServletContext come "DataSource"
//   3. carica le categorie e le salva nel ServletContext come "categorie"
// chi lo usa: tutte le servlet (getServletContext().getAttribute("DataSource"))
//             e le jsp per il menu (${applicationScope.categorie})
@WebListener
public class DataSourceListener implements ServletContextListener {

    // contextInitialized: chiamato da Tomcat all'avvio
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        try {
            // lookup JNDI
            // "jdbc/sportshop" deve essere uguale al name della Resource in context.xml
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/sportshop");

            // scope application: lo vedono tutte le servlet per tutta la vita dell'app
            context.setAttribute("DataSource", ds);

            // connessione di prova: se il db non risponde lo vedo subito nel log di Tomcat
            try (Connection con = ds.getConnection()) {
                System.out.println("[DataSourceListener] Connesso a: " + con.getMetaData().getURL());
            }

            // categorie caricate una volta sola perche' servono al menu di ogni pagina
            // cosi non faccio una query per ogni richiesta
            // limite: se aggiungo una categoria nel db devo riavviare Tomcat
            context.setAttribute("categorie", new CategoriaDAO(ds).doRetrieveAll());
        } catch (NamingException e) {
            // il nome JNDI non esiste (controllare context.xml)
            System.err.println("[DataSourceListener] Risorsa JNDI non trovata: " + e.getMessage());
        } catch (SQLException e) {
            // db spento, password sbagliata, schema mancante...
            System.err.println("[DataSourceListener] Connessione al DB fallita: " + e.getMessage());
        }
    }

    // contextDestroyed: chiamato alla chiusura dell'app
    // non faccio niente perche' il pool lo gestisce e lo chiude Tomcat
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
