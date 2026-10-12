package it.unisa.psport.model.bean;

// Categoria (JavaBean)
// rappresenta una riga della tabella categoria (Calcio, Running, ...)
// campi: id (pk), nome, descrizione
// creata da CategoriaDAO, usata nel menu (header.jsp), nella home e nel catalogo
public class Categoria {

    // costruttore vuoto: richiesto dalla convenzione JavaBean, i valori si mettono coi setter
    public Categoria() {

    }

    private int id;
    private String nome;
    private String descrizione;

    // getter e setter
    // l'EL li usa: ${cat.nome} chiama getNome(), quindi i nomi devono seguire la convenzione
    // se aggiungo un campo: attributo + getter/setter qui, poi nel DAO

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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
