package test.BusTUC;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
* @author tmn
* creds to tmn <3
*/
public class BusOracle
{
    private String question;
    private String answer;
    private URI uri;
    private Browser browser;

    /**
* Create an instance of Busstuc
*/
    public BusOracle() {
        try {
            uri = new URI("http", "m.atb.no", "/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    /**
* Set question
*
* @param question
*/
    public void setQuestion(String question)
    {
    	browser = new Browser();
        this.question = question;
    }


    /**
* Return question
*
* @return String
*/
    public String getQuestion() {
        return this.question;
    }


    /**
* Get answer fomr Bussorakelet
*
* @return String
*/
    public void ask() {
    	
        String content = null;
        URLConnection conn = null;
        Scanner sc = null;

        String tmpUri = this.uri.toString().replace("%3F", "?") + this.getQuestion().replace("%3F", "?");
        System.out.println("CONTENT: " + tmpUri);
        tmpUri = tmpUri.replace(" ", "%20");

        try {
            conn = new URL(tmpUri).openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            sc = new Scanner(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (sc != null) {
            sc.useDelimiter("\\Z");
            content =  sc.next();
            answer = content;
            
        } else {
            answer = "Søk kunne ikke gjennomføres. Sjekk om du er tilkoblet nettet (edge/3G/WiFi etc.)";
        }
    }

    /**
* Format answer from server.
*
* @return String
*/
    public String getAnswer() {
        String tmpAnswer2 = answer;
        String[] words = tmpAnswer2.split(" ");
        if(words.length > 4) {
            String finalAnswer = "";
            String start = "";
            String end = "";
            int j = 0;
            int k = 0;
            for(; j < words.length; j++) {
                if(!words[j+1].endsWith(".") && !words[j+2].endsWith(".") && !words[j+3].endsWith(".")) {
                    for(; k <= j; k++) {
                        start += words[k];
                        start += " ";
                    }
                    for(int l = k+1; l < words.length; l++) {
                        end += words[l];
                        end += " ";
                    }
                    break;
                }
            }

            end = end.replace(" kl. ", " kl ");
            end = end.replace(" ", " ");
            String[] answerFormatted = end.split("\\. ");

            for(int i = 0; i < answerFormatted.length; i++) {
                finalAnswer += answerFormatted[i].trim();
                if(i != answerFormatted.length - 1)
                    finalAnswer += ".\n\n";
                else
                    finalAnswer += ".";
            }

            return start + finalAnswer;
        }
        else
            return answer;
    }
}