package analisadores;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Stack;

public class Parser {

    private static Token lookAhead;
    private static TabelaDeSimbolos tds = new TabelaDeSimbolos();
    private static GeradorDeCodigo gdc = new GeradorDeCodigo();
    private static boolean temElse = false;
    private static boolean teveDo = false;

    private static Stack<Integer> pilhaChaves = new Stack();
    private static Stack<Integer> pilhaParenteses = new Stack();
    static Stack<Integer> pilhaPalavraDO = new Stack();
    private static Stack<Integer> pilhaPalavraIF = new Stack();

    public static Token analiseSintatica(BufferedReader br) throws IOException {
        programa(br);
        //gdc.imprimir();
        return lookAhead;
    }

    private static void pegarProximoToken(BufferedReader br) throws IOException {
        lookAhead = null;
        while (lookAhead == null) {
            lookAhead = Escaneador.analiseLexica(br);
//           if(lookAhead != null){
//                System.out.println(lookAhead);
//            }
        }
    }

    private static boolean programa(BufferedReader br) throws IOException {
        boolean houveErro;
        pegarProximoToken(br);
        if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_PALAVRA_INT) {
            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava a palavra 'int'.");
            System.exit(0);
            houveErro = true;
        } else {
            pegarProximoToken(br);
            if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_PALAVRA_MAIN) {
                System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava a palavra 'main'.");
                System.exit(0);
                houveErro = true;
            } else {
                pegarProximoToken(br);
                if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_ABRE_PARENTESES) {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um abre parenteses.");
                    System.exit(0);
                    houveErro = true;
                } else {
                    pegarProximoToken(br);
                    if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_FECHA_PARENTESES) {
                        System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um fecha parenteses.");
                        System.exit(0);
                        houveErro = true;
                    } else {
                        pegarProximoToken(br);
                        houveErro = bloco(br);
                    }
                }
            }
        }
        return houveErro;
    }

    private static boolean bloco(BufferedReader br) throws IOException {
        boolean houveErro;
        if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_ABRE_CHAVE) {
            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um abre chaves.");
            System.exit(0);
            houveErro = true;
        } else {
            tds.incrementarEscopo();
            pilhaChaves.push(lookAhead.getTipoLexico());
            pegarProximoToken(br);
            houveErro = declaracaoDeVariavel(br);
            if (houveErro == false) {//caso nao tenha ocorrido erro
                houveErro = comando(br);
                if (houveErro == false) {
                    while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_CHAVE) {//caso o token, apos a declaraçao for um fecha chaves, o bloco terminou
                        tds.apagarVariaveisEscopo();
                        if (pilhaChaves.isEmpty() == false) {
                            pilhaChaves.pop();
                        } else {
                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava o fim de arquivo.");
                            System.exit(0);
                            houveErro = true;
                            return houveErro;
                        }
                        pegarProximoToken(br);
                        if (lookAhead.getTipoLexico() == -1 && pilhaChaves.isEmpty() == false) {//caso o token apos o fim do bloco for o eof e a pilha NAO ESTIVER VAZIA, emita erro
                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um fecha chaves apos terminar o bloco de comandos.");
                            System.exit(0);
                            houveErro = true;
                            return houveErro;
                        } else if (lookAhead.getTipoLexico() == -1 && pilhaChaves.isEmpty() == true) {//caso o token for o eof e a pilha de chaves estiver vazia, termine o programa
                            return false;
                        } else if (pilhaChaves.isEmpty() == true && lookAhead.getTipoLexico() != -1) {
                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava o fim de arquivo.");
                            System.exit(0);
                            houveErro = true;
                            return houveErro;
                        }
                    }
                    houveErro = comando(br);
                }
            }
        }
        return houveErro;
    }

    private static boolean comando(BufferedReader br) throws IOException {
        boolean houveErro = false;
        switch (lookAhead.getTipoLexico()) {
            case ValoresTokens.VALOR_PALAVRA_IF:
                pilhaPalavraIF.add(ValoresTokens.VALOR_PALAVRA_IF);
                gdc.getGDC().add(lookAhead);
                pegarProximoToken(br);
                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES) {
                    houveErro = expressaoRelacional(br);
                    //System.out.println("L"+(gdc.getLabel()-1)+":");
                    if (houveErro == false) {//se nao acontecer erro
                        pegarProximoToken(br);
                        houveErro = comando(br);
                        if (temElse == true) {
                            System.out.println("L" + gdc.getLabel() + ":");
                        } else {
                            System.out.println("L" + (gdc.getLabel() - 1) + ":");
                        }
                    }
                } else {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um abre parenteses.");
                    System.exit(0);
                    houveErro = true;
                }
                return houveErro;
            case ValoresTokens.VALOR_PALAVRA_ELSE:
                if (pilhaPalavraIF.isEmpty() == true) {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Else sem if.");
                    System.exit(0);
                    houveErro = true;
                }
                temElse = true;
                pilhaPalavraIF.pop();
                pegarProximoToken(br);
                System.out.println("goto L" + gdc.getLabel() + ":");
                System.out.println("L" + (gdc.getLabel() - 1) + ":");
                houveErro = comando(br);
                return houveErro;
            case ValoresTokens.VALOR_PALAVRA_WHILE:
                gdc.getGDC().add(lookAhead);

                houveErro = iteracao(br);
                if (teveDo == false) {
                    System.out.println("goto L" + gdc.getLabel());
                    System.out.println("L" + (gdc.getLabel() - 1) + ":");
                } else{
                    teveDo = false;
                }
                return houveErro;
            case ValoresTokens.VALOR_PALAVRA_DO:
                gdc.getGDC().add(lookAhead);
                houveErro = iteracao(br);
                //System.out.println("goto L"+gdc.getLabel());
                //System.out.println("L"+(gdc.getLabel()-1)+":");
                return houveErro;
            case ValoresTokens.VALOR_IDENTIFICADOR:
                int posicaoTabela = tds.buscaTabelaCompleta(lookAhead);
                if (posicaoTabela != -1) {
                    houveErro = atribuicao(br, posicaoTabela);
                    return houveErro;
                } else {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                }
            case ValoresTokens.VALOR_ABRE_CHAVE:
                houveErro = bloco(br);
                return houveErro;
            case ValoresTokens.VALOR_FECHA_CHAVE:
                tds.apagarVariaveisEscopo();
                if (pilhaChaves.isEmpty() == false) {
                    pilhaChaves.pop();
                } else {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um fecha chaves.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                }
                pegarProximoToken(br);
                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PALAVRA_WHILE
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_PALAVRA_IF
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_PALAVRA_DO
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_CHAVE) {
                    houveErro = comando(br);
                }
                break;
            case -1:
                if (pilhaChaves.isEmpty() == true) {
                    break;
                } else {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um fecha chaves.");
                    System.exit(0);
                    houveErro = true;
                }

                break;
            default:
                System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava o começo de outro comando.");
                System.exit(0);
                houveErro = true;
                break;
        }
        return houveErro;
    }

    private static boolean iteracao(BufferedReader br) throws IOException {
        boolean houveErro;
        if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PALAVRA_WHILE) {
            pegarProximoToken(br);
            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES) {
                houveErro = expressaoRelacional(br);

                if (houveErro == false) {
                    pegarProximoToken(br);
                    if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PONTO_VIRGULA
                            && pilhaPalavraDO.isEmpty() == true) {
                        System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Instrucao vazia apos while.");
                        System.exit(0);
                        houveErro = true;
                        return houveErro;
                    } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PONTO_VIRGULA
                            && pilhaPalavraDO.isEmpty() == false) {
                        teveDo = true;
                        pilhaPalavraDO.pop();
                        pegarProximoToken(br);
                    }
                    houveErro = comando(br);

                }
            } else {
                System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um abre parenteses.");
                System.exit(0);
                houveErro = true;
                return houveErro;
            }
        } else {
            pilhaPalavraDO.push(lookAhead.getTipoLexico());
            pegarProximoToken(br);
            System.out.println("L" + gdc.getLabel() + ":");
            houveErro = comando(br);
            if (pilhaPalavraDO.isEmpty() == false) {
                System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um while.");
                System.exit(0);
                houveErro = true;
            }
        }
        return houveErro;
    }

    private static boolean atribuicao(BufferedReader br, int posicaoTabela) throws IOException {
        gdc.getGDC().add(lookAhead);
        boolean houveErro = false;
        pegarProximoToken(br);
        if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_ATRIBUICAO) {
            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um '='(atribuicao).");
            System.exit(0);
            houveErro = true;
            return houveErro;
        } else {
            int tipoVariavel = tds.getTabela().get(posicaoTabela).getTipo();
            houveErro = expressaoAritmeticaAtribuicao(br, tipoVariavel);

            if (houveErro == false) {
                pegarProximoToken(br);

                comando(br);

            }
            return houveErro;

        }

    }

    private static boolean expressaoRelacional(BufferedReader br) throws IOException {
        int aux = expressaoAritmetica(br);
        boolean houveErro;
        if (aux == -1) {
            return true;
        } else {
            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IGUALDADE
                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_MAIOR
                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_MAIOR_IGUAL
                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_MENOR
                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_MENOR_IGUAL
                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_DIFERENCA) {
                houveErro = expressaoAritmeticaRelacional(br, aux);
                gdc.gerarCodigoRelacional(tds, aux);
            } else {
                System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um operador relacional.");
                System.exit(0);
                houveErro = true;
                return houveErro;
            }
        }
        return houveErro;
    }

    private static int expressaoAritmetica(BufferedReader br) throws IOException {
        int tipoRelacional = -1, aux;
        boolean chave = false;
        pegarProximoToken(br);
        if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_INT
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_FLOAT
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_CHAR
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_ABRE_PARENTESES
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_IDENTIFICADOR) {
            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava o inicio de uma expressao aritmetica['(', identificador, inteiro, float, char].");
            System.exit(0);
            return -1;
        }
        while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_INT
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_FLOAT
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_CHAR
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
            if (chave == false) {
                switch (lookAhead.getTipoLexico()) {
                    case ValoresTokens.VALOR_INT:
                        tipoRelacional = 0;
                    case ValoresTokens.VALOR_FLOAT:
                        tipoRelacional = 1;
                        chave = true;
                        break;
                    case ValoresTokens.VALOR_CHAR:
                        tipoRelacional = 11;
                        chave = true;
                        break;
                    case ValoresTokens.VALOR_IDENTIFICADOR:
                        aux = tds.buscaTabelaCompleta(lookAhead);
                        if (aux == -1) {
                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error-> Identificador nao declarado.");
                            System.exit(0);
                            return aux;
                        } else {
                            if (tds.getTabela().get(aux).getTipo() == ValoresTokens.VALOR_INT) {
                                tipoRelacional = 0;
                                chave = true;
                            } else if (tds.getTabela().get(aux).getTipo() == ValoresTokens.VALOR_FLOAT) {
                                tipoRelacional = 1;
                                chave = true;
                            } else {
                                tipoRelacional = 11;
                                chave = true;
                            }
                        }
                        break;
                }
            }
            gdc.getGDC().add(lookAhead);
            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES) {//se for um abre parenteses, empilha e pega o proximo
                pilhaParenteses.push(lookAhead.getTipoLexico());
                pegarProximoToken(br);
            } else {//se nao for um abre parenteses, pega o proximo e continua os testes
                pegarProximoToken(br);
                while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_DIVISAO
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SOMA
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SUBTRACAO) {
                    gdc.getGDC().add(lookAhead);
                    pegarProximoToken(br);
                    if (tipoRelacional == 0 || tipoRelacional == 1) {
                        if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_INT
                                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_FLOAT
                                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES) {
                                break;
                            } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                aux = tds.buscaTabelaCompleta(lookAhead);
                                if (aux == -1) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                    System.exit(0);
                                    return aux;
                                } else {
                                    if (tds.getTabela().get(aux).getTipo() == ValoresTokens.VALOR_CHAR) {
                                        System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Expressao incompativel, programa esperada um float ou int.");
                                        System.exit(0);
                                        return -1;
                                    }
                                }
                            }
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES && pilhaParenteses.isEmpty() == false) {//se o token for um fecha parenteses e a pilha nao estiver vazia, significa que na expressao tem um abre parenteses, portanto desempilhe e leia o proximo 
                                pilhaParenteses.pop();
                                gdc.getGDC().add(lookAhead);
                                pegarProximoToken(br);
                            }
                        } else {
                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um float, int ou um abre parenteses.");
                            System.exit(0);
                            return -1;
                        }
                    } else {
                        if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_CHAR
                                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES) {
                                break;
                            } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                aux = tds.buscaTabelaCompleta(lookAhead);
                                if (aux == -1) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                    System.exit(0);
                                    return aux;
                                } else {
                                    if (tds.getTabela().get(aux).getTipo() != ValoresTokens.VALOR_CHAR) {
                                        System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Expressao incompativel, programa esperada um char.");
                                        System.exit(0);
                                        return -1;
                                    }
                                }
                            }
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES && pilhaParenteses.isEmpty() == false) {//se o token for um fecha parenteses e a pilha nao estiver vazia, significa que na expressao tem um abre parenteses, portanto desempilhe e leia o proximo 
                                pilhaParenteses.pop();
                                gdc.getGDC().add(lookAhead);
                                pegarProximoToken(br);
                            }
                        } else {
                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um char ou um abre parenteses.");
                            System.exit(0);
                            return -1;
                        }
                    }
                }
            }
        }
        if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IGUALDADE
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_MAIOR
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_MAIOR_IGUAL
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_MENOR
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_MENOR_IGUAL
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_DIFERENCA
                && pilhaParenteses.isEmpty() == true) {
            gdc.getGDC().add(lookAhead);
            return tipoRelacional;
        } else {
            return -1;
        }
    }

    private static boolean expressaoAritmeticaAtribuicao(BufferedReader br, int tipoVariavel) throws IOException {
        boolean houveErro = false;
        int tipoProcurado;
        pegarProximoToken(br);
        if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_INT
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_FLOAT
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_CHAR
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_ABRE_PARENTESES
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_IDENTIFICADOR) {
            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava o inicio de uma expressao aritmetica['(', identificador, inteiro, float, char].");
            System.exit(0);
            houveErro = true;
            return houveErro;
        }
        switch (tipoVariavel) {
            case 0:
                if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_INT
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_IDENTIFICADOR
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_ABRE_PARENTESES) {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um int, abre parenteses ou identificador.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                }
                while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_INT
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {

                    switch (lookAhead.getTipoLexico()) {
                        case ValoresTokens.VALOR_ABRE_PARENTESES://se for um abre parenteses, empilha e pega o proximo
                            pilhaParenteses.push(lookAhead.getTipoLexico());
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            break;
                        default:
                            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                tipoProcurado = tds.buscaTabelaCompleta(lookAhead);
                                if (tipoProcurado == -1) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                                tipoProcurado = tds.getTabela().get(tipoProcurado).getTipo();
                                if (tipoProcurado != ValoresTokens.VALOR_INT) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um identificador que fosse um float ou inteiro.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                            }
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_DIVISAO
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SOMA
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SUBTRACAO) {
                                gdc.getGDC().add(lookAhead);
                                pegarProximoToken(br);
                                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_INT
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                    if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES) {
                                        break;
                                    } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                        tipoProcurado = tds.buscaTabelaCompleta(lookAhead);
                                        if (tipoProcurado == -1) {
                                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                            System.exit(0);
                                            houveErro = true;
                                            return houveErro;
                                        }
                                        tipoProcurado = tds.getTabela().get(tipoProcurado).getTipo();
                                        if (tipoProcurado != ValoresTokens.VALOR_INT) {
                                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um que o identificador fosse um inteiro.");
                                            System.exit(0);
                                            houveErro = true;
                                            return houveErro;
                                        }
                                    }
                                    gdc.getGDC().add(lookAhead);
                                    pegarProximoToken(br);

                                    while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES && pilhaParenteses.isEmpty() == false) {//se o token for um fecha parenteses e a pilha nao estiver vazia, significa que na expressao tem um abre parenteses, portanto desempilhe e leia o proximo
                                        pilhaParenteses.pop();
                                        gdc.getGDC().add(lookAhead);
                                        pegarProximoToken(br);
                                    }
                                    if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_OPERADOR_DIVISAO
                                            && lookAhead.getTipoLexico() != ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO
                                            && lookAhead.getTipoLexico() != ValoresTokens.VALOR_OPERADOR_SOMA
                                            && lookAhead.getTipoLexico() != ValoresTokens.VALOR_OPERADOR_SUBTRACAO
                                            && lookAhead.getTipoLexico() != ValoresTokens.VALOR_PONTO_VIRGULA) {
                                        System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um operador ou um ponto e virgula.");
                                        System.exit(0);
                                    }

                                } else {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um inteiro, identificador ou um abre parenteses.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                            }
                            break;
                    }
                }
                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PONTO_VIRGULA
                        && pilhaParenteses.isEmpty() == true) {
                    gdc.gerarCodigoAtribuicao(tds, tipoVariavel);
                    return false;
                } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PONTO_VIRGULA
                        && pilhaParenteses.isEmpty() == false) {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Numero de abre parenteses diferente do de fecha parenteses.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                }
                break;
            case 1:
                if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_FLOAT
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_INT
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_IDENTIFICADOR
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_ABRE_PARENTESES) {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um float, abre parenteses ou identificador.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                }
                while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FLOAT
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_INT
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {

                    switch (lookAhead.getTipoLexico()) {
                        case ValoresTokens.VALOR_ABRE_PARENTESES://se for um abre parenteses, empilha e pega o proximo
                            pilhaParenteses.push(lookAhead.getTipoLexico());
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            break;
                        default:
                            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                tipoProcurado = tds.buscaTabelaCompleta(lookAhead);
                                if (tipoProcurado == -1) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                                tipoProcurado = tds.getTabela().get(tipoProcurado).getTipo();
                                if (tipoProcurado != ValoresTokens.VALOR_FLOAT && tipoProcurado != ValoresTokens.VALOR_INT) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um identificador que fosse um float ou inteiro.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                            }
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_DIVISAO
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SOMA
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SUBTRACAO) {
                                gdc.getGDC().add(lookAhead);
                                pegarProximoToken(br);
                                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FLOAT
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_INT
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                    if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES) {
                                        break;
                                    } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                        tipoProcurado = tds.buscaTabelaCompleta(lookAhead);
                                        if (tipoProcurado == -1) {
                                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                            System.exit(0);
                                            houveErro = true;
                                            return houveErro;
                                        }
                                        tipoProcurado = tds.getTabela().get(tipoProcurado).getTipo();
                                        if (tipoProcurado != ValoresTokens.VALOR_FLOAT && tipoProcurado != ValoresTokens.VALOR_INT) {
                                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um que o identificador fosse um float ou inteiro.");
                                            System.exit(0);
                                            houveErro = true;
                                            return houveErro;
                                        }
                                    }
                                    gdc.getGDC().add(lookAhead);
                                    pegarProximoToken(br);
                                    while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES && pilhaParenteses.isEmpty() == false) {//se o token for um fecha parenteses e a pilha nao estiver vazia, significa que na expressao tem um abre parenteses, portanto desempilhe e leia o proximo
                                        pilhaParenteses.pop();
                                        gdc.getGDC().add(lookAhead);
                                        pegarProximoToken(br);
                                    }
                                } else {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um float, identificador ou um abre parenteses.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                            }
                            break;
                    }
                }
                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PONTO_VIRGULA
                        && pilhaParenteses.isEmpty() == true) {

                    gdc.gerarCodigoAtribuicao(tds, tipoVariavel);
                    return false;
                } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PONTO_VIRGULA
                        && pilhaParenteses.isEmpty() == false) {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Numero de abre parenteses diferente do de fecha parenteses.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                }
                break;
            case 11:
                if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_CHAR
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_IDENTIFICADOR
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_ABRE_PARENTESES) {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um char, abre parenteses ou identificador.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                }
                while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_CHAR
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {

                    switch (lookAhead.getTipoLexico()) {
                        case ValoresTokens.VALOR_ABRE_PARENTESES://se for um abre parenteses, empilha e pega o proximo
                            pilhaParenteses.push(lookAhead.getTipoLexico());
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            break;
                        default:
                            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                tipoProcurado = tds.buscaTabelaCompleta(lookAhead);
                                if (tipoProcurado == -1) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                                tipoProcurado = tds.getTabela().get(tipoProcurado).getTipo();
                                if (tipoProcurado != ValoresTokens.VALOR_CHAR) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um identificador que fosse um char.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                            }
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_DIVISAO
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SOMA
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SUBTRACAO) {
                                gdc.getGDC().add(lookAhead);
                                pegarProximoToken(br);
                                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_CHAR
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                    if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES) {
                                        break;
                                    } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                        tipoProcurado = tds.buscaTabelaCompleta(lookAhead);
                                        if (tipoProcurado == -1) {
                                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                            System.exit(0);
                                            houveErro = true;
                                            return houveErro;
                                        }
                                        tipoProcurado = tds.getTabela().get(tipoProcurado).getTipo();
                                        if (tipoProcurado != ValoresTokens.VALOR_CHAR) {
                                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um que o identificador fosse um char.");
                                            System.exit(0);
                                            houveErro = true;
                                            return houveErro;
                                        }
                                    }
                                    gdc.getGDC().add(lookAhead);
                                    pegarProximoToken(br);
                                    while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES && pilhaParenteses.isEmpty() == false) {//se o token for um fecha parenteses e a pilha nao estiver vazia, significa que na expressao tem um abre parenteses, portanto desempilhe e leia o proximo
                                        pilhaParenteses.pop();
                                        gdc.getGDC().add(lookAhead);
                                        pegarProximoToken(br);
                                    }
                                } else {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um char, identificador ou um abre parenteses.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                            }
                            break;
                    }
                }
                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PONTO_VIRGULA
                        && pilhaParenteses.isEmpty() == true) {
                    gdc.gerarCodigoAtribuicao(tds, tipoVariavel);
                    return false;
                } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PONTO_VIRGULA
                        && pilhaParenteses.isEmpty() == false) {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Numero de abre parenteses diferente do de fecha parenteses.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                }
                break;
        }
        return houveErro;
    }

    private static boolean expressaoAritmeticaRelacional(BufferedReader br, int tipoRelacionado) throws IOException {
        boolean houveErro = false;
        int tipoProcurado;
        pegarProximoToken(br);
        if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_INT
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_FLOAT
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_CHAR
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_ABRE_PARENTESES
                && lookAhead.getTipoLexico() != ValoresTokens.VALOR_IDENTIFICADOR) {
            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava o inicio de uma expressao aritmetica['(', identificador, inteiro, float, char].");
            System.exit(0);
            houveErro = true;
            return houveErro;
        }
        switch (tipoRelacionado) {
            case 0:
            case 1:
                if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_INT
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_FLOAT
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_IDENTIFICADOR
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_ABRE_PARENTESES) {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um int, float, abre parenteses ou identificador.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                }
                while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_INT
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_FLOAT
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {

                    switch (lookAhead.getTipoLexico()) {
                        case ValoresTokens.VALOR_ABRE_PARENTESES://se for um abre parenteses, empilha e pega o proximo
                            pilhaParenteses.push(lookAhead.getTipoLexico());
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            break;
                        default:
                            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                tipoProcurado = tds.buscaTabelaCompleta(lookAhead);
                                if (tipoProcurado == -1) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                                tipoProcurado = tds.getTabela().get(tipoProcurado).getTipo();
                                if (tipoProcurado == ValoresTokens.VALOR_CHAR) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um identificador que fosse um float ou inteiro.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                            }
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_DIVISAO
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SOMA
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SUBTRACAO) {
                                gdc.getGDC().add(lookAhead);
                                pegarProximoToken(br);
                                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_INT
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_FLOAT
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                    if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES) {
                                        break;
                                    } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                        tipoProcurado = tds.buscaTabelaCompleta(lookAhead);
                                        if (tipoProcurado == -1) {
                                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                            System.exit(0);
                                            houveErro = true;
                                            return houveErro;
                                        }
                                        tipoProcurado = tds.getTabela().get(tipoProcurado).getTipo();
                                        if (tipoProcurado == ValoresTokens.VALOR_CHAR) {
                                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um que o identificador fosse um inteiro ou float.");
                                            System.exit(0);
                                            houveErro = true;
                                            return houveErro;
                                        }
                                    }
                                    gdc.getGDC().add(lookAhead);
                                    pegarProximoToken(br);
                                    if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES && pilhaParenteses.isEmpty() == false) {//se o token for um fecha parenteses e a pilha nao estiver vazia, significa que na expressao tem um abre parenteses, portanto desempilhe e leia o proximo
                                        pilhaParenteses.pop();
                                        gdc.getGDC().add(lookAhead);
                                        pegarProximoToken(br);
                                    }
                                } else {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um inteiro, float, identificador ou um abre parenteses.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                            }
                            break;
                    }
                }
                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES
                        && pilhaParenteses.isEmpty() == false) {//caso dps da expressao aritmetica for um fecha parenteses e caso a pilha NAO ESTEJA VAZIA emita erro em relaçao ao numero de abre parenteses
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Numero de abre parenteses diferente do de fecha parenteses.");
                    System.exit(0);
                    houveErro = true;
                } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES
                        && pilhaParenteses.isEmpty() == true) {//caso dps da expressao aritmetica for um fecha parenteses e caso a pilha ESTEJA VAZIA, tudo ok
                    return false;
                } else {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->   Programa esperava o fim de uma expressao relacional.");
                    System.exit(0);
                    houveErro = true;
                }
                break;
            case 11:
                if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_CHAR
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_IDENTIFICADOR
                        && lookAhead.getTipoLexico() != ValoresTokens.VALOR_ABRE_PARENTESES) {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um char, abre parenteses ou identificador.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                }
                while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_CHAR
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {

                    switch (lookAhead.getTipoLexico()) {
                        case ValoresTokens.VALOR_ABRE_PARENTESES://se for um abre parenteses, empilha e pega o proximo
                            pilhaParenteses.push(lookAhead.getTipoLexico());
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            break;
                        default:
                            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                tipoProcurado = tds.buscaTabelaCompleta(lookAhead);
                                if (tipoProcurado == -1) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                                tipoProcurado = tds.getTabela().get(tipoProcurado).getTipo();
                                if (tipoProcurado != ValoresTokens.VALOR_CHAR) {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um identificador que fosse um char.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                            }
                            gdc.getGDC().add(lookAhead);
                            pegarProximoToken(br);
                            while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_DIVISAO
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_MULTIPLICACAO
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SOMA
                                    || lookAhead.getTipoLexico() == ValoresTokens.VALOR_OPERADOR_SUBTRACAO) {
                                gdc.getGDC().add(lookAhead);
                                pegarProximoToken(br);
                                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_CHAR
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES
                                        || lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                    if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_ABRE_PARENTESES) {
                                        break;
                                    } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {
                                        tipoProcurado = tds.buscaTabelaCompleta(lookAhead);
                                        if (tipoProcurado == -1) {
                                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador nao declarado.");
                                            System.exit(0);
                                            houveErro = true;
                                            return houveErro;
                                        }
                                        tipoProcurado = tds.getTabela().get(tipoProcurado).getTipo();
                                        if (tipoProcurado != ValoresTokens.VALOR_CHAR) {
                                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um que o identificador fosse um char.");
                                            System.exit(0);
                                            houveErro = true;
                                            return houveErro;
                                        }
                                    }
                                    gdc.getGDC().add(lookAhead);
                                    pegarProximoToken(br);
                                    if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES && pilhaParenteses.isEmpty() == false) {//se o token for um fecha parenteses e a pilha nao estiver vazia, significa que na expressao tem um abre parenteses, portanto desempilhe e leia o proximo
                                        pilhaParenteses.pop();
                                        pegarProximoToken(br);
                                    }
                                } else {
                                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um char, identificador ou um abre parenteses.");
                                    System.exit(0);
                                    houveErro = true;
                                    return houveErro;
                                }
                            }
                            break;
                    }
                }
                if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES
                        && pilhaParenteses.isEmpty() == false) {//caso dps da expressao aritmetica for um fecha parenteses e caso a pilha NAO ESTEJA VAZIA emita erro em relaçao ao numero de abre parenteses
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Numero de abre parenteses diferente do de fecha parenteses.");
                    System.exit(0);
                    houveErro = true;
                } else if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_FECHA_PARENTESES
                        && pilhaParenteses.isEmpty() == true) {//caso dps da expressao aritmetica for um fecha parenteses e caso a pilha ESTEJA VAZIA, tudo ok
                    return false;
                } else {
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->   Programa esperava o fim de uma expressao relacional.");
                    System.exit(0);
                    houveErro = true;
                }
                break;
        }
        return houveErro;
    }

    private static boolean declaracaoDeVariavel(BufferedReader br) throws IOException {//retorna true caso tenha acontecido erro sintatico
        boolean houveErro = false, buscaEscopoLocal;
        int aux;
        while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_PALAVRA_INT
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_PALAVRA_FLOAT
                || lookAhead.getTipoLexico() == ValoresTokens.VALOR_PALAVRA_CHAR) { //enquanto o token for a palavra 'int', 'float' ou 'char'
            switch (lookAhead.getTipoLexico()) {
                case ValoresTokens.VALOR_PALAVRA_INT://caso for int, aux recebe 0 (valor_int)
                    aux = 0;
                    break;
                case ValoresTokens.VALOR_PALAVRA_FLOAT://caso for float, aux recebe 1 (valor_float)
                    aux = 1;
                    break;
                default://caso for char, aux recebe 11 (valor_char)
                    aux = 11;
                    break;
            }
            pegarProximoToken(br);
            if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {//caso o token seja um identificador
                buscaEscopoLocal = tds.buscaEscopoLocal(lookAhead);
                if (buscaEscopoLocal == true) {//caso esse identificador ja foi declarado, emita erro
                    System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador ja declarado neste escopo.");
                    System.exit(0);
                    houveErro = true;
                    return houveErro;
                } else {//caso nao haja erro, insira pegue o proximo e continue
                    tds.adicionarNaTabela(aux, lookAhead);
                    pegarProximoToken(br);
                    while (lookAhead.getTipoLexico() == ValoresTokens.VALOR_VIRGULA) {//enquanto os próximos tokens forem vírgula seguido de identificador, continue na iteração
                        pegarProximoToken(br);
                        if (lookAhead.getTipoLexico() == ValoresTokens.VALOR_IDENTIFICADOR) {//caso o token for um identificador depois de uma vírgula
                            buscaEscopoLocal = tds.buscaEscopoLocal(lookAhead);
                            if (buscaEscopoLocal == true) {//caso esse identificador ja foi declarado, emita erro
                                System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Identificador ja declarado neste escopo.");
                                System.exit(0);
                                houveErro = true;
                                return houveErro;
                            } else {
                                tds.adicionarNaTabela(aux, lookAhead);
                                pegarProximoToken(br);
                            }
                        } else {//caso nao seja identificador emita erro
                            System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um identificador.");
                            System.exit(0);
                            houveErro = true;
                            return houveErro;
                        }
                    }
                    if (lookAhead.getTipoLexico() != ValoresTokens.VALOR_PONTO_VIRGULA) {//caso nao seja um ponto e virgula, emita erro
                        System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um ponto e virgula.");
                        System.exit(0);
                        houveErro = true;
                        return houveErro;
                    }
                }
            } else {//caso o token nao for um identificador, emita erro
                System.out.println("Erro na linha: " + Escaneador.linha + " e na coluna: " + Escaneador.coluna + " Error->  Programa esperava um identificador.");
                System.exit(0);
                houveErro = true;
                return houveErro;
            }
            pegarProximoToken(br);
        }
        return houveErro;
    }
}
