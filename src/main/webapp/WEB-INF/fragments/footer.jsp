<%-- footer.jsp (frammento)
     incluso in fondo a tutte le pagine con jsp:include
     contenuto statico: chi siamo, assistenza, social, copyright
     i link con # sono segnaposto, pagine non ancora fatte --%>
<%@ page pageEncoding="UTF-8" %>

<footer class="site-footer">

    <%-- colonne: una sotto l'altra su mobile, 3 affiancate da tablet in su (media query nel css) --%>
    <div class="footer-cols">
        <div>
            <h3>Chi siamo</h3>
            <p>PsporT: articoli sportivi per tutti, dal principiante all'atleta.</p>
        </div>

        <div>
            <h3>Assistenza</h3>
            <ul>
                <li><a href="#">Spedizioni</a></li>
                <li><a href="#">Resi e rimborsi</a></li>
                <li><a href="#">Contatti</a></li>
            </ul>
        </div>

         <div>
            <h3>Seguici</h3>
            <ul>
                <li><a href="#">Instagram</a></li>
                <li><a href="#">Facebook</a></li>

            </ul>
        </div>


    </div>

    <p class="copyright">&copy; 2026 PsporT - Progetto TSW</p>

</footer>
