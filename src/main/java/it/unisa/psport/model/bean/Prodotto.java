package it.unisa.psport.model.bean;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Prodotto (JavaBean)
// rappresenta una riga della tabella prodotto
// creato da ProdottoDAO (metodo creaProdotto), mostrato nelle card di home e catalogo
public class Prodotto {

    // costruttore vuoto: richiesto dalla convenzione JavaBean, il DAO poi usa i setter
    public Prodotto() {

    }

    private int id;                          // pk
    private String nome;
    private String  marca;
    private String descrizione;
    private BigDecimal prezzo;               // BigDecimal e non double: col double ci sono errori di arrotondamento sui soldi
    private BigDecimal iva;                  // percentuale, es. 22.00
    private String immagine;                 // percorso relativo dell'immagine, es. images/prodotti/p1.jpg
    private int idCategoria;                 // fk verso categoria
    private int idTipo;                      // fk verso tipo
    private boolean attivo;                  // false = tolto dalla vendita (non si cancella perche' serve agli ordini vecchi)
    private LocalDateTime dataInserimento;   // usata per ordinare le novita

    // getter e setter
    // nelle jsp ${p.nome} chiama getNome(), quindi i nomi devono seguire la convenzione
    // per i boolean il getter si chiama isX invece di getX (isAttivo)
    // se aggiungo una colonna alla tabella: attributo + getter/setter qui, poi creaProdotto nel DAO

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }

    public LocalDateTime getDataInserimento() {
        return dataInserimento;
    }

    public void setDataInserimento(LocalDateTime dataInserimento) {
        this.dataInserimento = dataInserimento;
    }
}
