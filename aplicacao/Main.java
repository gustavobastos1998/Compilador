package aplicacao;

import analisadores.Parser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        File arquivo;
        arquivo = new File("C:\\Users\\user\\Desktop\\123.txt");
        BufferedReader br = new BufferedReader(new FileReader(arquivo));
        Parser.analiseSintatica(br);
        br.close();
    }

}
