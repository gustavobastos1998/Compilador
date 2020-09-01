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
public class GeradorDeCodigo {

    private ArrayList<Token> gdc;
    private int registrador;
    private int label;

    public GeradorDeCodigo() {
        gdc = new ArrayList();
        registrador = 0;
        label = 0;
    }

    public ArrayList<Token> getGDC() {
        return gdc;
    }

    public void imprimir() {
        int i;
        for (i = 0; i < gdc.size(); i++) {
            System.out.print(gdc.get(i));
        }
    }

    public int getLabel() {
        return label;
    }

    public void gerarCodigoAtribuicao(TabelaDeSimbolos tds, int tipo) {
        // imprimir();
        if (gdc.get(0).getContent().compareTo("do") == 0) {
            //imprimir();
            System.out.println(gdc.get(1).getContent() + " = " + gdc.get(2).getContent());
            resolverParenteses(tds, tipo);
            resolverMultiplicacaoDivisao(tds, tipo);
            resolverAdicaoDiferenca(tds, tipo);
        } else {
            resolverParenteses(tds, tipo);
            resolverMultiplicacaoDivisao(tds, tipo);
            resolverAdicaoDiferenca(tds, tipo);
            System.out.println(gdc.get(0).getContent() + " = " + gdc.get(1).getContent());
        }

        gdc.removeAll(gdc);
    }

    public void gerarCodigoRelacional(TabelaDeSimbolos tds, int tipo) {
        resolverParenteses(tds, tipo);
        resolverMultiplicacaoDivisao(tds, tipo);
        resolverAdicaoDiferenca(tds, tipo);
        if (gdc.get(0).getContent().compareTo("while") == 0 && Parser.pilhaPalavraDO.isEmpty() == false) {
            //imprimir();
            System.out.println("t"+registrador+" = "+gdc.get(1).getContent()+gdc.get(2).getContent()+gdc.get(3).getContent());
            System.out.println("if " + " t" + registrador + " != 0 goto L" + label);
        } else if (gdc.get(0).getContent().compareTo("while") == 0) {
            System.out.println("L" + (label + 1) + ":");
            //imprimir();
            System.out.println("t" + registrador + " = " + gdc.get(1).getContent() + gdc.get(2).getContent() + gdc.get(3).getContent());
            System.out.println("if " + " t" + registrador + " == 0 goto L" + label);
        } else {
            System.out.println("t" + registrador + " = " + gdc.get(1).getContent() + gdc.get(2).getContent() + gdc.get(3).getContent());
            System.out.println("if " + " t" + registrador + " == 0 goto L" + label);
        }
        registrador++;
        label++;
        gdc.removeAll(gdc);
    }

    private void resolverParenteses(TabelaDeSimbolos tds, int tipo) {
        int index1, index2, i;
        boolean removerParen;
        Token abreParen, fechaParen, replaceToken;
        abreParen = new Token(new StringBuilder("("), ValoresTokens.VALOR_ABRE_PARENTESES);
        fechaParen = new Token(new StringBuilder(")"), ValoresTokens.VALOR_FECHA_PARENTESES);
        index2 = buscarIndice(fechaParen);
        switch (tipo) {
            case 1:
                while (index2 != -1) {
                    index1 = buscarUltimoIndice(abreParen);
                    index2 = buscarIndice(fechaParen);
                    if (index1 != -1) {
                        for (i = index1 + 2; i <= index2; i++) {
                            if (gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO || gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_DIVISAO) {
                                checarTipo(tds, i);
                                System.out.println("t" + registrador + " = " + gdc.get(i - 1).getContent() + gdc.get(i).getContent() + gdc.get(i + 1).getContent());
                                replaceToken = new Token(new StringBuilder("t" + registrador), tipo);
                                registrador++;
                                gdc.remove(i - 1);
                                gdc.add(i - 1, replaceToken);
                                gdc.remove(i + 1);
                                gdc.remove(i);
                                index2 = index2 - 2;
                                i = i - 1;
                            }
                        }
                        for (i = index1 + 2; i <= index2; i++) {
                            if (gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SOMA || gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SUBTRACAO) {
                                checarTipo(tds, i);
                                System.out.println("t" + registrador + " = " + gdc.get(i - 1).getContent() + gdc.get(i).getContent() + gdc.get(i + 1).getContent());
                                replaceToken = new Token(new StringBuilder("t" + registrador), tipo);
                                registrador++;
                                gdc.remove(i - 1);
                                gdc.add(i - 1, replaceToken);
                                gdc.remove(i + 1);
                                gdc.remove(i);
                                index2 = index2 - 2;
                                i = i - 1;
                            }
                        }
                    }
                    removerParen = contem(abreParen);
                    if (removerParen == true) {
                        gdc.remove(buscarIndice(fechaParen));
                        gdc.remove(buscarUltimoIndice(abreParen));
                    }
                }
                break;
            default:
                while (index2 != -1) {
                    index1 = buscarUltimoIndice(abreParen);
                    index2 = buscarIndice(fechaParen);
                    if (index1 != -1) {
                        for (i = index1 + 2; i <= index2; i++) {
                            if (gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO || gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_DIVISAO) {
                                System.out.println("t" + registrador + " = " + gdc.get(i - 1).getContent() + gdc.get(i).getContent() + gdc.get(i + 1).getContent());
                                replaceToken = new Token(new StringBuilder("t" + registrador), tipo);
                                registrador++;
                                gdc.remove(i - 1);
                                gdc.add(i - 1, replaceToken);
                                gdc.remove(i + 1);
                                gdc.remove(i);
                                index2 = index2 - 2;
                                i = i - 1;
                            }
                        }
                        for (i = index1 + 2; i <= index2; i++) {
                            if (gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SOMA || gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SUBTRACAO) {
                                System.out.println("t" + registrador + " = " + gdc.get(i - 1).getContent() + gdc.get(i).getContent() + gdc.get(i + 1).getContent());
                                replaceToken = new Token(new StringBuilder("t" + registrador), tipo);
                                registrador++;
                                gdc.remove(i - 1);
                                gdc.add(i - 1, replaceToken);
                                gdc.remove(i + 1);
                                gdc.remove(i);
                                index2 = index2 - 2;
                                i = i - 1;
                            }
                        }
                    }
                    removerParen = contem(abreParen);
                    if (removerParen == true) {
                        gdc.remove(buscarIndice(fechaParen));
                        gdc.remove(buscarUltimoIndice(abreParen));
                    }
                }
                break;
        }

    }

    private void resolverMultiplicacaoDivisao(TabelaDeSimbolos tds, int tipo) {
        int i;
        Token replaceToken;
        switch (tipo) {
            case 1:
                for (i = 1; i < gdc.size(); i++) {
                    if (gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO
                            || gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_DIVISAO) {
                        checarTipo(tds, i);
                        System.out.println("t" + registrador + " = " + gdc.get(i - 1).getContent() + gdc.get(i).getContent() + gdc.get(i + 1).getContent());
                        replaceToken = new Token(new StringBuilder("t" + registrador), tipo);
                        registrador++;
                        gdc.remove(i - 1);
                        gdc.add(i - 1, replaceToken);
                        gdc.remove(i + 1);
                        gdc.remove(i);
                        i = i - 1;
                    }
                }
            default:
                for (i = 1; i < gdc.size(); i++) {
                    if (gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO
                            || gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_DIVISAO) {
                        System.out.println("t" + registrador + " = " + gdc.get(i - 1).getContent() + gdc.get(i).getContent() + gdc.get(i + 1).getContent());
                        replaceToken = new Token(new StringBuilder("t" + registrador), tipo);
                        registrador++;
                        gdc.remove(i - 1);
                        gdc.add(i - 1, replaceToken);
                        gdc.remove(i + 1);
                        gdc.remove(i);
                        i = i - 1;
                    }
                }
                break;
        }
    }

    private void resolverAdicaoDiferenca(TabelaDeSimbolos tds, int tipo) {
        int i;
        Token replaceToken;
        switch (tipo) {
            case 1:
                for (i = 1; i < gdc.size(); i++) {
                    if (gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SOMA
                            || gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SUBTRACAO) {
                        checarTipo(tds, i);
                        System.out.println("t" + registrador + " = " + gdc.get(i - 1).getContent() + gdc.get(i).getContent() + gdc.get(i + 1).getContent());
                        replaceToken = new Token(new StringBuilder("t" + registrador), tipo);
                        registrador++;
                        gdc.remove(i - 1);
                        gdc.add(i - 1, replaceToken);
                        gdc.remove(i + 1);
                        gdc.remove(i);
                        i = i - 1;
                    }
                }
            default:
                for (i = 1; i < gdc.size(); i++) {
                    if (gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SOMA
                            || gdc.get(i).getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SUBTRACAO) {
                        System.out.println("t" + registrador + " = " + gdc.get(i - 1).getContent() + gdc.get(i).getContent() + gdc.get(i + 1).getContent());
                        replaceToken = new Token(new StringBuilder("t" + registrador), tipo);
                        registrador++;
                        gdc.remove(i - 1);
                        gdc.add(i - 1, replaceToken);
                        gdc.remove(i + 1);
                        gdc.remove(i);
                        i = i - 1;
                    }
                }
                break;
        }
    }

    private void checarTipo(TabelaDeSimbolos tds, int index) {
        Token replace, procurado;
        String aux;
        StringBuilder newContent;
        int busca;
        if (gdc.get(index - 1).getTipoLexico() == ValoresTokens.VALOR_INT) {
            aux = gdc.get(index - 1).getContent();
            newContent = new StringBuilder(aux + ".0");
            replace = new Token(newContent, ValoresTokens.VALOR_FLOAT);
            System.out.println("t" + registrador + " = to_float " + aux);
            registrador++;
            gdc.add(index - 1, replace);
            gdc.remove(index);
        } else if (gdc.get(index - 1).getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
            procurado = gdc.get(index - 1);
            busca = tds.buscaTabelaCompleta(procurado);
            if (tds.getTabela().get(busca).getTipo() == ValoresTokens.VALOR_INT) {//se o identificador na tabela for int, converta para float
                aux = procurado.getContent();
                newContent = new StringBuilder(aux);
                replace = new Token(newContent, ValoresTokens.VALOR_FLOAT);
                System.out.println("t" + registrador + " = to_float " + aux);
                registrador++;
                gdc.add(index - 1, replace);
                gdc.remove(index);
            }
        }
        if (gdc.get(index + 1).getTipoLexico() == ValoresTokens.VALOR_INT) {
            aux = gdc.get(index + 1).getContent();
            newContent = new StringBuilder(aux + ".0");
            replace = new Token(newContent, ValoresTokens.VALOR_FLOAT);
            System.out.println("t" + registrador + " = to_float " + aux);
            registrador++;
            gdc.add(index + 1, replace);
            gdc.remove(index + 2);
        } else if (gdc.get(index + 1).getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
            procurado = gdc.get(index + 1);
            busca = tds.buscaTabelaCompleta(procurado);
            if (tds.getTabela().get(busca).getTipo() == ValoresTokens.VALOR_INT) {//se o identificador na tabela for int, converta para float
                aux = procurado.getContent();
                newContent = new StringBuilder(aux);
                replace = new Token(newContent, ValoresTokens.VALOR_FLOAT);
                System.out.println("t" + registrador + " = to_float " + aux);
                registrador++;
                gdc.add(index + 1, replace);
                gdc.remove(index + 2);
            }
        }
    }

    private int buscarIndice(Token t) {
        int i;
        for (i = 0; i < gdc.size(); i++) {
            if (gdc.get(i).equals(t)) {
                return i;
            }
        }
        return -1;
    }

    private int buscarUltimoIndice(Token t) {
        int i, result = -1;
        for (i = 0; i < gdc.size(); i++) {
            if (gdc.get(i).equals(t)) {
                result = i;
            }
        }
        return result;
    }

    private boolean contem(Token t) {
        int i;
        for (i = 0; i < gdc.size(); i++) {
            if (gdc.get(i).equals(t)) {
                return true;
            }
        }
        return false;
    }
}
