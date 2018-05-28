/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fonoconnecthelper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Mateus
 */

/**
 * GAMBIARRA PRA FAZER BACKUP DO RELACIONAMENTO PALAVRA_FONEMA
 * E GERAR UM RELATORIO COM AS PALAVRAS CADASTRADAS COM SEUS RESPECTIVOS FONEMAS
 */
public class Reporter {
    
    public static final String DATABASE_NAME = "database.db";

    public static void makeSQL() {
        FonoConnectDBHelper db = new FonoConnectDBHelper("jdbc:sqlite:" + DATABASE_NAME);

        List<Integer[]> list = db.getWordPhoneme();
        
        list.forEach((t) -> {
            System.out.println("INSERT INTO PALAVRA_FONEMA (P_ID, F_ID, DIFICULDADE) VALUES (" + t[0] + ", " + t[1] + ", " + t[2] +");");
        });
        
    }
    
    
    public static void makeHTML() throws IOException {
        FonoConnectDBHelper db = new FonoConnectDBHelper("jdbc:sqlite:" + DATABASE_NAME);

        String a = "<!DOCTYPE html><html><head><meta charset='UTF-8'><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\"></head><body><div class=\"container\"><table class=\"table table-hover table-striped\"><thead><tr><th>Palavra</th><th>Fonemas</th></tr></thead><tbody>";
        String b = "</tbody></table></div></body></html>";
        String out = "";
        
        List<Word> words = db.getWords();
        List<Phoneme> phonemes;
        for(Word w : words) {
            phonemes = db.getPhonemesInWord(w.getId());
            String p = "/ ";
            for(Phoneme ph : phonemes) {
                p += ph.getName() + " ";
            }
            p += "/";
            
            out += "<tr><td>" + w.getName() + "</td><td>" + p + "</td></tr>";
        }
        
        out = a + out + b;
        
        Files.write(Paths.get("report_" + new Date().toString().replace(" ", "_").replace(":", "_") + ".html"), out.getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE);
    }
    
    
    
}
