/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadores;

/**
 *
 * @author user
 */
public class Tipo {
    private Token token;
    private int escopo;
    private int tipo;
    
    public Tipo(Token t, int escopo, int tdi){
        token = t;
        this.escopo = escopo;
        tipo = tdi;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public int getEscopo() {
        return escopo;
    }

    public void setEscopo(int escopo) {
        this.escopo = escopo;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipoDoIdentificador) {
        this.tipo = tipoDoIdentificador;
    }
    
    
}
