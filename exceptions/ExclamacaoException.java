/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author user
 */
public class ExclamacaoException extends Exception {

    public ExclamacaoException() {

    }

    public ExclamacaoException(String s) {
        System.out.println(s);
    }
}
