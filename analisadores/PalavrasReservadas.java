/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadores;

/**
 *
 * @author aluno
 */
public class PalavrasReservadas {


    static final String PALAVRA_INT = "int";
    static final String PALAVRA_FLOAT = "float";
    static final String PALAVRA_CHAR = "char";
    static final String PALAVRA_DO = "do";
    static final String PALAVRA_IF = "if";
    static final String PALAVRA_ELSE = "else";
    static final String PALAVRA_FOR = "for";
    static final String PALAVRA_WHILE = "while";
    static final String PALAVRA_DOUBLE = "double";
    static final String PALAVRA_MAIN = "main";
    
    static int whichPalavraReservada(String s) {
        switch (s) {
            case PALAVRA_INT:
                return ValoresTokens.VALOR_PALAVRA_INT;
            case PALAVRA_FLOAT:
                return ValoresTokens.VALOR_PALAVRA_FLOAT;
            case PALAVRA_CHAR:
                return ValoresTokens.VALOR_PALAVRA_CHAR;
            case PALAVRA_DO:
                return ValoresTokens.VALOR_PALAVRA_DO;
            case PALAVRA_IF:
                return ValoresTokens.VALOR_PALAVRA_IF;
            case PALAVRA_ELSE:
                return ValoresTokens.VALOR_PALAVRA_ELSE;
            case PALAVRA_FOR:
                return ValoresTokens.VALOR_PALAVRA_FOR;
            case PALAVRA_WHILE:
                return ValoresTokens.VALOR_PALAVRA_WHILE;
            case PALAVRA_MAIN:
                return ValoresTokens.VALOR_PALAVRA_MAIN;
            default:
                return -1;
        }
    }

}
