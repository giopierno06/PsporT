<%-- catalogo.jsp (vista)
     ci si arriva solo tramite forward da CatalogoServlet o da RicercaServlet
     dati in request: ${prodotti} lista da mostrare
                      ${titolo} titolo della pagina
                      ${categoria} solo se c'e' il filtro per categoria, altrimenti vuoto
                      ${ricerca} solo se si arriva da /ricerca (testo cercato), altrimenti vuoto --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- titolo della scheda cambia in base alla categoria --%>
    <title><c:out value="${titolo}"/> - PsporT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%-- locale italiano per i prezzi nelle card --%>
<fmt:setLocale value="it_IT"/>

<jsp:include page="/WEB-INF/fragments/header.jsp" />

<main>
    <section class="catalogo">

        <%-- breadcrumb (percorso)
             con filtro:    Home > Catalogo > nome categoria
             senza filtro:  Home > Catalogo
             con ricerca:   Home > Catalogo > Ricerca --%>
        <nav class="breadcrumb" aria-label="Percorso">
            <a href="${pageContext.request.contextPath}/">Home</a> &rsaquo;
            <c:choose>
                <c:when test="${not empty ricerca}">
                    <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a> &rsaquo;
                    <span>Ricerca</span>
                </c:when>
                <c:when test="${not empty categoria}">
                    <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a> &rsaquo;
                    <span><c:out value="${categoria.nome}"/></span>
                </c:when>
                <c:otherwise>
                    <span>Catalogo</span>
                </c:otherwise>
            </c:choose>
        </nav>

        <%-- titolo e descrizione (la descrizione solo se c'e' una categoria che ce l'ha) --%>
        <h1><c:out value="${titolo}"/></h1>
        <c:if test="${not empty categoria.descrizione}">
            <p class="descrizione-categoria"><c:out value="${categoria.descrizione}"/></p>
        </c:if>
        <%-- numero di prodotti: in EL si puo' chiamare il metodo size() della lista --%>
        <p class="conteggio">${prodotti.size()} prodotti</p>

        <%-- griglia dei prodotti, ogni card viene da cardProdotto.jspf --%>
        <c:choose>
            <c:when test="${empty prodotti}">
                <%-- il messaggio cambia: se arrivo da una ricerca ripeto cosa e' stato cercato --%>
                <c:choose>
                    <c:when test="${not empty ricerca}">
                        <p>Nessun risultato per "<c:out value="${ricerca}"/>".
                           Prova con un'altra parola o guarda il <a href="${pageContext.request.contextPath}/catalogo">catalogo completo</a>.</p>
                    </c:when>
                    <c:otherwise>
                        <p>Nessun prodotto in questa categoria.</p>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <div class="griglia-prodotti">
                    <c:forEach var="p" items="${prodotti}">
                        <%@ include file="/WEB-INF/fragments/cardProdotto.jspf" %>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </section>
</main>

<jsp:include page="/WEB-INF/fragments/footer.jsp" />

</body>
</html>
