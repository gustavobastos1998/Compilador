
package exceptions;


public class EOFEmComentarioException extends Exception{
    public EOFEmComentarioException(){
        
    }
    public EOFEmComentarioException(String s){
        System.out.println(s);
    }
}
