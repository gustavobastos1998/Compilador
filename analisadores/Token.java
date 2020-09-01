
package analisadores;

public class Token {
    private String content;
    private int tipoLexico;
    
    public Token(StringBuilder c, int tl){
        content = c.toString();
        tipoLexico = tl;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTipoLexico() {
        return tipoLexico;
    }

    public void setTipoLexico(int tipoLexico) {
        this.tipoLexico = tipoLexico;
    }
    
    @Override
    public String toString(){
        return "Conteúdo: "+content+"\nTipo da variável: "+tipoLexico+'\n';
    }
    
    public boolean equals(Token t){
        return this.content.compareTo(t.content) == 0 && this.tipoLexico == t.tipoLexico ;
    }
    
    
}
