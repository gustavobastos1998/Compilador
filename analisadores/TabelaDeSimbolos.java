/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadores;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class TabelaDeSimbolos {

    private ArrayList<Tipo> tabela;
    private int escopoAtual;

    TabelaDeSimbolos() {
        tabela = new ArrayList();
        escopoAtual = 0;
    }
    
    public ArrayList<Tipo> getTabela(){
        return tabela;
    }

    public void incrementarEscopo() {
        escopoAtual++;
    }

    public void adicionarNaTabela(int tipoDaVariavel, Token t) {
        Tipo newTipo = new Tipo(t, escopoAtual, tipoDaVariavel);
        tabela.add(0, newTipo);
    }

    public int buscaTabelaCompleta(Token t) {
        int i;
        for (i = 0; i < tabela.size(); i++) {
            if (t.getContent().compareTo(tabela.get(i).getToken().getContent()) == 0) {
                return i;
            }
        }
        return -1;
    }

    public boolean buscaEscopoLocal(Token t) {//retorn true se encontrou no escopo local e false caso nao encontre 
        int i;
        for (i = 0; i < tabela.size(); i++) {
            if (tabela.get(i).getEscopo() != escopoAtual) {
                return false;
            } else {
                if (t.getContent().compareTo(tabela.get(i).getToken().getContent()) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void apagarVariaveisEscopo(){
        int i;
        for(i=0;i<tabela.size();i++){
            if(tabela.get(i).getEscopo() == escopoAtual){
                tabela.remove(i);
            } else{
                break;
            }
        }
        escopoAtual--;
    }

}
