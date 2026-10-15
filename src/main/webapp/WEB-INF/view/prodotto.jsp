<%-- prodotto.jsp (vista)
     pagina di dettaglio di un prodotto, ci si arriva solo tramite forward da ProdottoServlet (/prodotto?id=N)
     dati in request: ${prodotto} il Prodotto da mostrare (la servlet manda 404 se non esiste, qui non e' mai null)
                      ${varianti} lista di Variante (taglia e quantita), per ora la servlet non la manda ancora --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- alias: scrivo ${p.nome} invece di ${prodotto.nome} --%>
<c:set var="p" value="${prodotto}"/>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- titolo della scheda cambia in base al prodotto (un solo title, dentro l'head) --%>
    <title><c:out value="${p.nome}"/> - PsporT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%-- locale italiano per il prezzo: va impostato prima di usare fmt:formatNumber --%>
<fmt:setLocale value="it_IT"/>

<jsp:include page="/WEB-INF/fragments/header.jsp" />

<main>
    <%-- section figlia diretta di main: prende centratura e padding dalla regola "main > section"
         la classe dettaglio-prodotto serve per gli stili di questa pagina (in tablet diventa una griglia a 2 colonne) --%>
    <section class="dettaglio-prodotto">

       <%-- breadcrumb: Home > Catalogo > nome del prodotto (stessa classe di catalogo.jsp) --%>
        <nav class="breadcrumb" aria-label="Percorso">
            <a href="${pageContext.request.contextPath}/">Home</a> &rsaquo;
            <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a> &rsaquo;
            <span><c:out value="${p.nome}"/></span>
        </nav>

        <%-- colonna sinistra: immagine
             nel db il campo immagine e' gia' un percorso tipo "images/prodotti/p1.jpg", basta anteporre il contextPath --%>
        <div class="prodotto-immagine">
            <img src="${pageContext.request.contextPath}/${p.immagine}" alt="<c:out value='${p.nome}'/>">
        </div>

        <%-- colonna destra: marca, nome, prezzo, descrizione, taglie, pulsante
             ogni testo che arriva dal db passa da c:out (escape dell'html, protezione da XSS) --%>
        <div class="prodotto-info">
            <p class="marca"><c:out value="${p.marca}"/></p>
            <h1><c:out value="${p.nome}"/></h1>

            <%-- prezzo come valuta (virgola e simbolo euro), uguale alla card --%>
            <p class="prezzo">
                <fmt:formatNumber value="${p.prezzo}" type="currency" currencySymbol="€"/>
            </p>

            <%-- la descrizione puo' essere null o vuota: in quel caso non scrivo il paragrafo --%>
            <c:if test="${not empty p.descrizione}">
                <p class="descrizione"><c:out value="${p.descrizione}"/></p>
            </c:if>

            <%-- taglie: un radio per ogni variante
                 se non ci sono varianti (o la servlet non le manda ancora) scrivo "non disponibile"
                 e il pulsante resta disabilitato --%>
            <c:choose>
                <c:when test="${empty varianti}">
                    <p class="non-disponibile">Prodotto non disponibile.</p>
                </c:when>
                <c:otherwise>
                    <%-- fieldset + legend: raggruppano i radio e li rendono leggibili anche ai lettori di schermo --%>
                    <fieldset class="taglie">
                        <legend>Taglia</legend>
                        <c:forEach var="v" items="${varianti}">
                            <%-- quantita 0 = taglia esaurita: classe "esaurita" (barrata) e radio disabilitato
                                 il disabled si puo' aggirare dal browser, il controllo vero andra' rifatto lato server --%>
                            <label class="taglia ${v.quantita == 0 ? 'esaurita' : ''}">
                                <%-- il radio e' nascosto dal css, si vede solo lo span; il value e' l'id della variante
                                     (non del prodotto) perche' la disponibilita' e' per taglia --%>
                                <input type="radio" name="idVariante" value="${v.id}"
                                       ${v.quantita == 0 ? 'disabled' : ''}>
                                <span><c:out value="${v.taglia}"/></span>
                            </label>
                        </c:forEach>
                    </fieldset>
                </c:otherwise>
            </c:choose>


            <button type="button" class="btn" data-id="${p.id}"
                    ${empty varianti ? 'disabled' : ''}>Aggiungi al carrello</button>
        </div>


    </section>
</main>

<jsp:include page="/WEB-INF/fragments/footer.jsp" />

</body>
</html>
