package analisadores;

import java.io.IOException;
import exceptions.*;
import java.io.BufferedReader;

public class Escaneador {

    private static char c = ' ';
    private static StringBuilder buffer = new StringBuilder();
    static int linha = 1;
    static int coluna = 0;

    public static Token analiseLexica(BufferedReader br) throws IOException {
        int qtd = 0, i, buscarID;
        Token newToken;
        while (Character.isWhitespace(c) == true) {
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
        }
        if (Character.isWhitespace(c) == false && buffer.length() == 0) {
            buffer.append(c);
        }
        if (Character.isDigit(buffer.charAt(0)) == true) {//caso o 1º char for um dígito, ele pode ser um inteiro ou um float, adicione ao buffer até não ser um dígito ou um ponto
            c = (char) br.read();//leia o próximo byte
            coluna++;
            if (Character.isDigit(c) == true || c == '.') {//caso o byte for um dígito ou um '.', adicione ao buffer
                buffer.append(c);
            } else {//caso nao seja nem '.' nem dígito, o token está pronto para ser analisado
                try {
                    if (buffer.toString().contains(".") == false) {//se não conter '.' no buffer ele é um inteiro
                        newToken = new Token(buffer, ValoresTokens.VALOR_INT);
                        buffer.replace(0, buffer.length(), "");
                        return newToken;
                    } else {//contem '.', ele pode ser um float
                        if (buffer.charAt(buffer.length() - 1) == '.') {//se o último no buffer for um '.', levante a exception float mal formado
                            throw new FloatMalFormadoException();
                        } else {//o '.' não está no final, ainda pode ser que tenha mais que um '.'
                            for (i = 0; i < buffer.length(); i++) {//percorrer o buffer para contar a qtd de '.'
                                if (buffer.charAt(i) == '.') {//se o caracter lido do buffer for um '.', incremente o qtd
                                    qtd = qtd + 1;
                                }
                            }
                            if (qtd > 1) {//se o buffer tiver mais que um '.', levante a exception float mal formado
                                throw new FloatMalFormadoException();
                            } else {//o buffer só tem um '.', portanto o token é um float
                                newToken = new Token(buffer, ValoresTokens.VALOR_FLOAT);
                                buffer.replace(0, buffer.length(), "");
                                return newToken;
                            }
                        }
                    }
                } catch (FloatMalFormadoException fmfe) {
                    System.err.println("Float mal formado!\n" + "Linha:" + linha + " Coluna:" + coluna + "\nUltimo token: " + buffer);
                    buffer.replace(0, buffer.length(), "");
                }
                if (c == '\n') {
                    linha++;
                    coluna = 1;
                }
            }
        } else if (buffer.charAt(0) == '.') {//se o 1º char do buffer for um '.', só pode ser um float
            c = (char) br.read();//leia o próximo byte
            coluna++;
            if (Character.isDigit(c) == true || c == '.') {//caso o byte for um dígito ou um '.', adicione ao buffer
                buffer.append(c);
            } else {//caso nao seja nem '.' nem dígito, o token está pronto para ser analisado
                for (i = 1; i < buffer.length(); i++) {//verificar se no buffer tem algum '.' além do primeiro
                    try {
                        if (buffer.charAt(i) == '.') {//caso tenha um '.' além do primeiro, levante a exception float mal formado
                            throw new FloatMalFormadoException();
                        }
                    } catch (FloatMalFormadoException fmfe) {//caso a exception seja levantada, informe o motivo e saia da função
                        System.err.println("Float mal formado!\n" + "Linha:" + linha + " Coluna:" + coluna + "\nUltimo token: " + buffer);
                        buffer.replace(0, buffer.length(), "");
                    }
                }//caso saia do for, o token não tem nada de errado
                newToken = new Token(buffer, ValoresTokens.VALOR_FLOAT);
                buffer.replace(0, buffer.length(), "");
                return newToken;
            }
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
        } else if (Character.isLetter(buffer.charAt(0)) == true || buffer.charAt(0) == '_') {//se o 1º caracter for uma letra ou um '_', pode ser um identificador
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            if (Character.isLetterOrDigit(c) == true || c == '_') {//caso o byte for um dígito ou uma letra ou '_', adicione ao buffer
                buffer.append(c);
            } else {//caso o byte nao seja letra, digito ou '_', o token está pronto para ser analisado
                buscarID = PalavrasReservadas.whichPalavraReservada(buffer.toString());//pesquisa por todas as palavras reservadas, caso não for retorna -1
                if (buscarID == -1) {//caso o retorno seja -1 implica que é um identificador
                    newToken = new Token(buffer, ValoresTokens.VALOR_IDENTIFICADOR);
                    buffer.replace(0, buffer.length(), "");
                    return newToken;
                } else {//caso o retorno não seja -1 implica que é uma palavra reservada
                    newToken = new Token(buffer, buscarID);
                    buffer.replace(0, buffer.length(), "");
                    return newToken;
                }
            }
        } else if (buffer.charAt(0) == "'".charAt(0)) {//se o 1º caracter for um apóstrofo, então so pode ser um char
            c = (char) br.read();//leia o próximo byte
            coluna++;
            buffer.append(c);//coloque o byte no buffer 
            try {
                if (buffer.charAt(buffer.length() - 1) == "'".charAt(0)) {//caso após o 1º apóstrofo vier outro, char mal formado
                    throw new CharMalFormadoException();
                }
                c = (char) br.read();//leia mais um byte
                coluna++;
                if (c == '\n') {
                    linha++;
                    coluna = 1;
                }
                buffer.append(c);//coloque o byte no buffer
                if (buffer.charAt(buffer.length() - 1) != "'".charAt(0)) {//caso o último caracter do buffer for diferente de "'", char mal formatado
                    throw new CharMalFormadoException();
                } else {//caso o último caracter do buffer for igual a "'", é um char
                    newToken = new Token(buffer, ValoresTokens.VALOR_CHAR);
                    buffer.replace(0, buffer.length(), "");
                    c = (char) br.read();
                    coluna++;
                    if (c == '\n') {
                        linha++;
                        coluna = 1;
                    }
                    return newToken;
                }
            } catch (CharMalFormadoException cmfe) {
                System.err.println("Char mal formado!\n" + "Linha:" + linha + " Coluna:" + coluna + "\nUltimo token: " + buffer);
                c = (char) br.read();
                buffer.replace(0, buffer.length(), "");
            }
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
        } else if (buffer.charAt(0) == '+') {//caso o primeiro caracter seja um símbole de adição, leia o próximo e retorne o token
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_OPERADOR_SOMA);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '-') {//caso o primeiro caracter seja um símbole de subtração, leia o próximo e retorne o token
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_OPERADOR_SUBTRACAO);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '*') {//caso o primeiro caracter seja um símbole de multiplicação, leia o próximo e retorne
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '/') {//caso o primeiro caracter for um símbole de divisão, ele pode ser comentário de linha, de várias linhas ou uma divisão
            c = (char) br.read();
            coluna++;
            buffer.append(c);
            if (buffer.charAt(1) == '/') {//caso o segundo caracter do buffer for outro '/', é um comentário de uma linha
                if (c == '\n') {//se pular um linha o comentario acabou
                    buffer.replace(0, buffer.length(), "");
                    return null;
                } else if (c == 65535) {
                    buffer.replace(0, buffer.length(), "");
                    return new Token(buffer, -1);
                }
            } else if (buffer.charAt(1) == '*') {//caso o segundo caracter do buffer for um '*', é um comentario de múltiplas linhas
                if (buffer.length() >= 4) {//caso o tamanho do buffer for maior ou igual a 4, deve-se testar pra ver se o comentário acabou
                    if (buffer.charAt(buffer.length() - 2) == '*') {//caso o penúltemo caracter do buffer for um '*'...
                        if (buffer.charAt(buffer.length() - 1) == '/') {//... e o último caracter do buffer for um '/', o comentario acabou
                            c = (char) br.read();
                            coluna++;
                            if (c == '\n') {
                                linha++;
                                coluna = 1;
                            }
                            buffer.replace(0, buffer.length(), "");
                            return null;
                        }
                    }
                }
                try {
                    if (c == 65535) {//caso o c for eof e o comentario ainda não acabou, levante um erro
                        throw new EOFEmComentarioException();
                    }
                } catch (EOFEmComentarioException eofece) {
                    System.err.println("Final de arquivo em comentario nao terminado!" + "    Linha:" + linha + " Coluna:" + coluna + "\nUltimo token: EOF");
                    buffer.replace(0, buffer.length(), "");
                    return new Token(buffer, -1);
                }
            } else {
                buffer.replace(0, buffer.length(), "/");
                newToken = new Token(buffer, ValoresTokens.VALOR_OPERADOR_DIVISAO);
                buffer.replace(0, buffer.length(), "");
                return newToken;
            }
        } else if (buffer.charAt(0) == '(') {//caso for um abre parênteses, leia o próximo e retorne o token
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_ABRE_PARENTESES);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == ')') {//caso for um fecha parênteses, leia o próximo e retorne o token
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_FECHA_PARENTESES);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '{') {//caso for um abre chave, leia o próximo e retorne o token
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_ABRE_CHAVE);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '}') {//caso for um fecha chave, leia o próximo e retorne o token
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_FECHA_CHAVE);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '[') {//caso for um abre colchete, leia o próximo e retorne o token
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_ABRE_COLCHETE);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == ']') {//caso for um fecha colchete, leia o próximo e retorne o token
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_FECHA_COLCHETE);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == ';') {//caso for um ponto e vírgula, leia o próximo e retorne o token
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_PONTO_VIRGULA);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == ',') {//caso for uma vírgula, leia o próximo e retorne o token
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_VIRGULA);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '=') {//caso for um '=', leia o próximo caso for outro '=' retorno o token IGUALDADE, caso nao for retorne ATRIBUIÇÃO
            c = (char) br.read();
            coluna++;
            if (c == '=') {
                buffer.append(c);
                newToken = new Token(buffer, ValoresTokens.VALOR_IGUALDADE);
                buffer.replace(0, buffer.length(), "");
                c = (char) br.read();
                return newToken;
            }
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_ATRIBUICAO);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '!') {//lê o próximo e se não for um '=' levante um erro
            c = (char) br.read();
            coluna++;
            try {
                if (c == '=') {
                    buffer.append(c);
                    newToken = new Token(buffer, ValoresTokens.VALOR_DIFERENCA);
                    buffer.replace(0, buffer.length(), "");
                    c = (char) br.read();
                    return newToken;
                }
                throw new ExclamacaoException();
            } catch (ExclamacaoException ee) {
                System.err.println("Exclamaçao nao seguida de '='!" + "   Linha:" + linha + " Coluna:" + coluna + "\nUltimo token: " + buffer);
                c = (char) br.read();
                buffer.replace(0, buffer.length(), "");
            }
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
        } else if (buffer.charAt(0) == '<') {//caso for um '<', leia o proximo pra ver se é menor ou igual que
            c = (char) br.read();
            coluna++;
            if (c == '=') {
                buffer.append(c);
                newToken = new Token(buffer, ValoresTokens.VALOR_MENOR_IGUAL);
                buffer.replace(0, buffer.length(), "");
                c = (char) br.read();
                return newToken;
            }
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_MENOR);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '>') {//caso for '>', leia o proximo para ver se é maior ou igual que
            c = (char) br.read();
            coluna++;
            if (c == '=') {
                buffer.append(c);
                newToken = new Token(buffer, ValoresTokens.VALOR_MAIOR_IGUAL);
                buffer.replace(0, buffer.length(), "");
                c = (char) br.read();
                return newToken;
            }
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_MAIOR);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == ':') {
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_DOIS_PONTOS);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '.') {
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            newToken = new Token(buffer, ValoresTokens.VALOR_PONTO);
            buffer.replace(0, buffer.length(), "");
            return newToken;
        } else if (buffer.charAt(0) == '&') {
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            if (c == '&') {
                buffer.append(c);
                newToken = new Token(buffer, ValoresTokens.VALOR_LOGICAL_AND);
                buffer.replace(0, buffer.length(), "");
                return newToken;
            } else {
                newToken = new Token(buffer, ValoresTokens.VALOR_BOOLEAN_LOGICAL_AND);
                buffer.replace(0, buffer.length(), "");
                return newToken;
            }
        } else if (buffer.charAt(0) == '|') {
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
            if (c == '|') {
                buffer.append(c);
                newToken = new Token(buffer, ValoresTokens.VALOR_LOGICAL_OR);
                buffer.replace(0, buffer.length(), "");
                return newToken;
            } else {
                newToken = new Token(buffer, ValoresTokens.VALOR_BOOLEAN_LOGICAL_OR);
                buffer.replace(0, buffer.length(), "");
                return newToken;
            }
        } else if (c == 65535) {
            return new Token(buffer, -1);
        } else {
            System.err.println("Caracter invalido   " + "Linha:" + linha + " Coluna:" + coluna + "\nUltimo token: " + buffer);
            buffer.replace(0, 1, "");
            c = (char) br.read();
            coluna++;
            if (c == '\n') {
                linha++;
                coluna = 1;
            }
        }

        return null;
    }
}
