<%-- header.jsp (frammento)
     incluso in tutte le pagine con jsp:include, quindi contiene solo l'header (niente html/head/body)
     contiene: logo, barra di ricerca, link utente, menu delle categorie
     usa: ${applicationScope.categorie} (dal DataSourceListener)
          ${requestScope.categoria} (solo nel catalogo, da CatalogoServlet) --%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="site-header">

    <div class="header-top">
        <%-- logo: porta alla home. contextPath = /PsporT, cosi il link funziona anche se cambia il nome dell'app --%>
        <a class="logo" href="${pageContext.request.contextPath}/">PsporT</a>

        <%-- ricerca: form in GET, l'url diventa /ricerca?q=testo (gestito da RicercaServlet)
             value rimette nella casella il testo gia' cercato; c:out evita problemi con virgolette e <script> --%>
        <form class="search" action="${pageContext.request.contextPath}/ricerca" method="get">
            <input type="search" name="q" value="<c:out value='${requestScope.ricerca}'/>" placeholder="Cerca un prodotto, uno sport, una marca..." aria-label="Cerca">
            <button type="submit">Cerca</button>
        </form>
        <%-- suggerimenti AJAX della ricerca (JSON da /ricerca/suggerimenti); defer: parte dopo che la pagina e' stata letta --%>
        <script src="${pageContext.request.contextPath}/js/ricerca.js" defer></script>

        <%-- link utente
             da fare: se l'utente e' loggato (attributo in sessione) mostrare il nome al posto di Accedi --%>
        <div class="user-links">
            <a href="${pageContext.request.contextPath}/login">Accedi</a>
            <a href="${pageContext.request.contextPath}/carrello">Carrello</a>
        </div>
    </div>


    <%-- menu categorie: una voce per ogni categoria del db
         le categorie le carica il DataSourceListener all'avvio (scope application) --%>
    <nav class="main-nav">
        <ul>
            <c:forEach var="cat" items="${applicationScope.categorie}">
                <%-- se sto guardando il catalogo di questa categoria la evidenzio con la classe "attiva"
                     "categoria" in request c'e' solo quando la mette CatalogoServlet --%>
                <li><a href="${pageContext.request.contextPath}/catalogo?categoria=${cat.id}"
                       <c:if test="${requestScope.categoria.id == cat.id}">class="attiva" aria-current="page"</c:if>><c:out value="${cat.nome}"/></a></li>
            </c:forEach>
        </ul>
    </nav>

</header>
