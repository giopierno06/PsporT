// ricerca.js
// suggerimenti AJAX sotto la barra di ricerca dell'header
// mentre l'utente scrive chiede a /ricerca/suggerimenti un json con i primi prodotti
// e li mostra in una lista cliccabile (link a /prodotto?id=N)
// se qualcosa va storto non succede niente: il form di ricerca normale funziona comunque

(function () {
    const form = document.querySelector('.search');
    if (!form) return;

    const input = form.querySelector('input[name="q"]');
    const base = form.getAttribute('action');   // es. /PsporT/ricerca
    const contextPath = base.substring(0, base.lastIndexOf('/ricerca'));

    // lista dei suggerimenti, creata una volta sola
    const lista = document.createElement('ul');
    lista.className = 'suggerimenti';
    lista.hidden = true;
    form.appendChild(lista);

    let timer = null;
    let richiestaCorrente = 0;   // per ignorare le risposte arrivate in ritardo

    function nascondi() {
        lista.hidden = true;
        lista.replaceChildren();
    }

    function mostra(prodotti) {
        lista.replaceChildren();
        if (prodotti.length === 0) {
            nascondi();
            return;
        }
        const euro = new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' });
        for (const p of prodotti) {
            const li = document.createElement('li');
            const a = document.createElement('a');
            a.href = contextPath + '/prodotto?id=' + encodeURIComponent(p.id);
            // textContent e non innerHTML: il testo arriva dal db e non va mai interpretato come html
            const nome = document.createElement('span');
            nome.textContent = p.nome;
            const dettaglio = document.createElement('small');
            dettaglio.textContent = p.marca + ' - ' + euro.format(p.prezzo);
            a.append(nome, dettaglio);
            li.appendChild(a);
            lista.appendChild(li);
        }
        lista.hidden = false;
    }

    async function cerca(testo) {
        const numero = ++richiestaCorrente;
        try {
            const risposta = await fetch(contextPath + '/ricerca/suggerimenti?q=' + encodeURIComponent(testo),
                                         { headers: { 'Accept': 'application/json' } });
            if (!risposta.ok) throw new Error('HTTP ' + risposta.status);
            const prodotti = await risposta.json();
            // se nel frattempo e' partita un'altra richiesta, questa risposta e' vecchia
            if (numero === richiestaCorrente) mostra(prodotti);
        } catch (e) {
            if (numero === richiestaCorrente) nascondi();
        }
    }

    input.addEventListener('input', function () {
        clearTimeout(timer);
        const testo = input.value.trim();
        if (testo.length < 2) {
            richiestaCorrente++;
            nascondi();
            return;
        }
        // debounce: aspetto 250ms dall'ultimo tasto per non fare una richiesta a ogni lettera
        timer = setTimeout(function () { cerca(testo); }, 250);
    });

    // click fuori dalla ricerca o tasto Esc chiudono la lista
    document.addEventListener('click', function (e) {
        if (!form.contains(e.target)) nascondi();
    });
    input.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') nascondi();
    });
})();
