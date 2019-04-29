package aplicativo.chat.message;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class Chat implements Serializable {
    
    private String nome; 
    private String texto; 
    private String nomeReservada;
    private Set<String> ListaOnlines = new HashSet<>(); 
    private Acao acao; 

    public String getName() {
        return nome;
    }

    public void setName(String name) {
        this.nome = name;
    }

    public String getText() {
        return texto;
    }

    public void setText(String text) {
        this.texto = text;
    }

    public String getNomeReservada() {
        return nomeReservada;
    }

    public void setNomeReservada(String nomeReservada) {
        this.nomeReservada = nomeReservada;
    }

    public Set<String> getListaOnlines() {
        return ListaOnlines;
    }

    public void setListaOnlines(Set<String> ListaOnlines) {
        this.ListaOnlines = ListaOnlines;
    }

    public Acao getAction() {
        return acao;
    }

    public void setAction(Acao action) {
        this.acao = action;
    }


        
    public enum Acao {
        CONNECT, DISCONNECT, SEND_ONE, SEND_ALL, USERS_ONLINE
    }
}
