/*
 * - FonoConnectHelper  -
 *  v.: 1.0 - b11.4.2018
 */
package fonoconnecthelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mateus
 */
public class FonoConnectDBHelper {

    //TABLE CONSTANTS FROM THE DATABASE
    private final String TABLE_WORD_NAME        = "PALAVRA";
    private final String TABLE_WORD_ID          = "PID";
    private final String TABLE_WORD_WORD        = "PPALAVRA";

    private final String TABLE_PHONEME_NAME     = "FONEMA";
    private final String TABLE_PHONEME_ID       = "FID";
    private final String TABLE_PHONEME_PHONEME  = "FFONEMA";
    private final String TABLE_PHONEME_EXAMPLE  = "FEXEMPLO";

    private final String TABLE_WORDPHONEME_NAME = "PALAVRA_FONEMA";
    private final String TABLE_WORDPHONEME_WID  = "PID";
    private final String TABLE_WORDPHONEME_PID  = "FID";
    private final String TABLE_WORDPHONEME_LEVEL  = "DIFICULDADE";

    private final String URL;

    public FonoConnectDBHelper(String url) {
        /* Debug */
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection Established");
        } catch (SQLException ex) {
            System.out.println("Wrong Connection");
            Logger.getLogger(FonoConnectDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        //////////
        this.URL = url;
    }

    private Connection connect() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(this.URL);
        } catch (SQLException ex) {
            System.out.println("error " + ex.getMessage());
        }

        return conn;
    }

    /**
     * This method save the relation between the word and the phoneme.
     * @param wordId The id from the word
     * @param phonemeId The id from the phoneme
     * @return If it's succeed, return true, else false;
     */
    public boolean saveWordPhoneme(Integer wordId, Integer phonemeId) {
        String sql = "INSERT INTO " + this.TABLE_WORDPHONEME_NAME + " (" + this.TABLE_WORDPHONEME_WID + ", " + this.TABLE_WORDPHONEME_PID + ", " + this.TABLE_WORDPHONEME_LEVEL + ") " + "VALUES (?, ?, ?);";

        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, wordId);
            pstmt.setInt(2, phonemeId);
            pstmt.setInt(3, 0);
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * This method delete all the phonemes in the word.
     * @param wordId The id from the word
     * @return If it's succeed, return true, else false;
     */
    public boolean deletePhonemesInWord(Integer wordId) {
        String sql = "DELETE FROM " + this.TABLE_WORDPHONEME_NAME + " WHERE " + this.TABLE_WORDPHONEME_WID + " = ? ;";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, wordId);
            pstmt.executeUpdate();
            return true;
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }        
    }
    
    /**
     * This method return a list of phonemes that a word contains
     * @param wordId The id of the word to search the phonemes
     * @return If it's succeed, return a <b>List of Phonemes</b>, else return an empty list
     */
    public List<Phoneme> getPhonemesInWord(Integer wordId) {
        List<Phoneme> list = new ArrayList<>();
        Phoneme       phoneme;
        String sql = "SELECT p." + this.TABLE_PHONEME_ID + " AS PHONID, p." + this.TABLE_PHONEME_PHONEME + " AS PHONPHON "
                   + "FROM " + this.TABLE_WORDPHONEME_NAME + " wp "
                   + "INNER JOIN " + this.TABLE_PHONEME_NAME + " p "
                   + "ON wp." + this.TABLE_WORDPHONEME_PID + " = p." + this.TABLE_PHONEME_ID + " "
                   + "WHERE PID = " + wordId + ";";
        
        try (Connection conn = this.connect();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                phoneme = new Phoneme(rs.getInt("PHONID"), rs.getString("PHONPHON"));
                list.add(phoneme);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return list;
    }

    /**
     * This method get from database the phoneme with the same <b>id</b>
     * parameter.
     *
     * @param id ID from the saved phoneme in database
     * @return If the phoneme exists in database return it, else this method
     * returns <b>null</b>.
     */
    public Phoneme getPhoneme(Integer id) {
        Phoneme phoneme = new Phoneme();

        String sql = "SELECT * FROM " + this.TABLE_PHONEME_NAME + " WHERE " + this.TABLE_PHONEME_ID + " = " + id + ";";

        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            //If there isn't result return a null phoneme;
            if (!rs.last()) {
                phoneme = null;
            } else {
                phoneme = new Phoneme(
                        rs.getInt(this.TABLE_PHONEME_ID),
                        rs.getString(this.TABLE_PHONEME_PHONEME),
                        rs.getString(this.TABLE_PHONEME_EXAMPLE)
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return phoneme;
    }

    /**
     * This method get from database all the phonemes
     *
     * @return If there are phonemes, returns a <b>list of phonemes</b>, else
     * return <b>null</b>.
     */
    public List<Phoneme> getPhonemes() {
        List<Phoneme> phonemes = new ArrayList<>();
        Phoneme phoneme;

        String sql = "SELECT * FROM " + this.TABLE_PHONEME_NAME;

        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                phoneme = new Phoneme(rs.getInt(this.TABLE_PHONEME_ID), rs.getString(this.TABLE_PHONEME_PHONEME), rs.getString(this.TABLE_PHONEME_EXAMPLE));
                phonemes.add(phoneme);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            phonemes = null;
        }

        return phonemes;
    }

    /**
     * This method get from database the word with the same <b>id</b> parameter.
     *
     * @param id ID from the word in database
     * @return If the word exists in database return it, else this method
     * returns <b>null</b>.
     */
    public Word getWord(Integer id) {
        Word word = new Word();

        String sql = "SELECT * FROM " + this.TABLE_WORD_NAME + " WHERE " + this.TABLE_WORD_ID + " = " + id + ";";

        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            //If there isn't result return a null phoneme;
            if (!rs.last()) {
                word = null;
            } else {
                word = new Word(
                        rs.getInt(this.TABLE_WORD_ID),
                        rs.getString(this.TABLE_WORD_WORD)
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return word;
    }

    /**
     * This method get from database all the words
     *
     * @return If there are words, returns a <b>list of words</b>, else return
     * <b>null</b>.
     */
    public List<Word> getWords() {
        List<Word> words = new ArrayList<>();
        Word word;

        String sql = "SELECT * FROM " + this.TABLE_WORD_NAME + " ORDER BY " + this.TABLE_WORD_WORD;

        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                word = new Word(rs.getInt(this.TABLE_WORD_ID), rs.getString(this.TABLE_WORD_WORD));
                words.add(word);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return words;
    }
    
    public List<Integer[]> getWordPhoneme() {
        List<Integer[]> list = new ArrayList<>();
        Integer[] temp;
        
        String sql = "SELECT * FROM " + this.TABLE_WORDPHONEME_NAME;

        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                temp = new Integer[3];
                temp[0] = rs.getInt(this.TABLE_WORDPHONEME_WID);
                temp[1] = rs.getInt(this.TABLE_WORDPHONEME_PID);
                temp[2] = rs.getInt(this.TABLE_WORDPHONEME_LEVEL);
                list.add(temp);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return list;
    } 
}
