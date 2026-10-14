<%-- home.jsp (vista)
     ci si arriva solo tramite forward da HomeServlet (sta in WEB-INF)
     dati usati: ${novita} (request, da HomeServlet)
                 ${applicationScope.categorie} (application, dal DataSourceListener)
     sezioni: banner, categorie, novita --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <%-- viewport: senza questa sul telefono la pagina viene rimpicciolita e le media query non funzionano --%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PsporT - Articoli sportivi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%-- locale italiano, serve a fmt:formatNumber nelle card per i prezzi con la virgola --%>
<fmt:setLocale value="it_IT"/>

<%-- header: include dinamico (jsp:include), il frammento viene eseguito a parte a ogni richiesta --%>
<jsp:include page="/WEB-INF/fragments/header.jsp" />

<main>

    <%-- banner principale con il link al catalogo completo --%>
    <section class="hero">
        <h1>Lo sport per tutti</h1>
        <p>Abbigliamento, calzature e attrezzatura per ogni disciplina.</p>
        <a class="btn" href="${pageContext.request.contextPath}/catalogo">Scopri il catalogo</a>
    </section>



    <%-- categorie: stessa lista del menu, presa dallo scope application
         ogni box porta a /catalogo?categoria=id
         immagine: images/categorie/ + nome in minuscolo + .jpg (es. running.jpg)
         quindi per una nuova categoria basta aggiungere l'immagine con quel nome --%>
    <section class="categorie">
        <h2>Scegli il tuo sport</h2>
        <div class="griglia-categorie">
            <c:forEach var="cat" items="${applicationScope.categorie}">
               <a class="categoria" href="${pageContext.request.contextPath}/catalogo?categoria=${cat.id}">
                    <img src="${pageContext.request.contextPath}/images/categorie/${fn:toLowerCase(cat.nome)}.jpg"
                         alt="<c:out value='${cat.nome}'/>">
                    <span><c:out value="${cat.nome}"/></span>
                </a>
            </c:forEach>
        </div>
    </section>

    <%-- novita: lista messa in request da HomeServlet
         per cambiare quanti prodotti si vedono: NUMERO_NOVITA in HomeServlet
    <section class="novita">
        <h2>Novità</h2>

        <%-- c:choose come if/else: messaggio se la lista e' vuota, altrimenti la griglia --%>
        <c:choose>
            <c:when test="${empty novita}">
                <p>Nessun prodotto disponibile al momento.</p>
            </c:when>
            <c:otherwise>
                <div class="griglia-prodotti">
                    <c:forEach var="p" items="${novita}">
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
