<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="p" value="${prodotto}"/>
<!DOCTYPE html>
<html lang="it">

<title><c:out value="${prodotto.nome}"/>-PsporT</title>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- titolo della scheda cambia in base al prodotto --%>
    <title><c:out value="${prodotto.nome}"/> - PsporT</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>
<jsp:include page="/WEB-INF/fragments/header.jsp" />
<fmt:setLocale value="it_IT"/>
<main>
    <section class="dettaglio-prodotto">
        <h1><c:out value='${p.nome}'/></h1>
        <img src="${pageContext.request.contextPath}/${p.immagine}" alt="<c:out value='${p.nome}'/>">
    </section>
</main>

<jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>