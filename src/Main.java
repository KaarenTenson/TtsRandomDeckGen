import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Random;
public class Main {
    public static void request(String url1, ArrayList<String> urlid, ArrayList<String> nimed, FileWriter writer){
        try {
            System.out.println(url1);
            URL url = new URL(url1);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                StringBuilder informationstring = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    informationstring.append(scanner.nextLine());
                }
                scanner.close();

                JSONParser parser = new JSONParser();
                JSONObject card = (JSONObject) parser.parse(String.valueOf(informationstring));


                String name = (String) card.get("name");

                writer.write(name+"\n");
                System.out.println(name);
                nimed.add(name);

                if (card.get("image_uris")!=null){
                    JSONObject yes = (JSONObject)card.get("image_uris");
                    urlid.add((String) yes.get("normal"));
                }
                else{
                    JSONArray imageeforflip = (JSONArray) card.get("card_faces");
                    JSONObject fimagee = (JSONObject) imageeforflip.get(0);
                    fimagee = (JSONObject) fimagee.get("image_uris");
                    urlid.add((String) fimagee.get("normal"));
                }
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<String> nimed = new ArrayList<String>();  /////////llist kaartidde nimede jaoks
        ArrayList<String> urlid = new ArrayList<String>();  //////////list kaartide urlide jaoks
        int LandsCount=0;
        int basicp=30;
        String colors;
        String nonlandsearch;
        String landsearch;
        char[] varvid={'W','U','B','G','R'};
        Scanner s = new Scanner(System.in);
        System.out.println("deck size:");
        int decksize = Integer.parseInt(s.nextLine());

        System.out.println("random?(T/F)");
        String kaslihtne=s.nextLine();
        if(!kaslihtne.equals("T")) {

            System.out.println("how many lands?");
            LandsCount = Integer.parseInt(s.nextLine());

            System.out.println("% of nonbasiclands (only int)");
            basicp = Integer.parseInt(s.nextLine());

            System.out.println("colors (WUBRG)");
            colors = s.nextLine();

            System.out.println("nonland card search (copy everything after q=)");
            nonlandsearch = s.nextLine();

            System.out.println("land card search (copy everything after q=)");
            landsearch = s.nextLine();
        }
        else{
            LandsCount=(int)(decksize*0.4);
            Random rand=new Random();
            StringBuilder build=new StringBuilder();
            while (build.length()<2){
                char varv=varvid[rand.nextInt(varvid.length)];
                if(build.isEmpty() ||build.charAt(0)!=varv){
                    build.append(varv);
                }
            }
            colors=build.toString();
            System.out.println(colors);
            nonlandsearch="-type%3Aland+commander%3A"+colors+"+%28game%3Apaper%29";
            //nonlandsearch="-type%3Aland+color<%3DWU+%28game%3Apaper%29";
            System.out.println(nonlandsearch);
            landsearch="-type%3Aland+commander%3A"+colors+"+%28game%3Apaper%29";

        }
        System.out.println(LandsCount);
        int nonlands = decksize-LandsCount;
        System.out.println(nonlands);
        float protent = basicp/100.0f;
        int nonbasiclands = (int)(LandsCount*protent);
        System.out.println(nonbasiclands);
        int basiclands = LandsCount-nonbasiclands;
        System.out.println(basiclands);
        File file = new File("decklist.txt");
        FileWriter writer = new FileWriter(file);

        for(int i = 0; i<nonlands ; i++) {  //////////////lisab landid
            Thread.sleep(50);
            request((String) ("https://api.scryfall.com/cards/random?q="+nonlandsearch),urlid, nimed,writer);
        }
        for(int i = 0; i<nonbasiclands ; i++) {  /////////////lisab nonbasic landid
            Thread.sleep(50);
            request("https://api.scryfall.com/cards/random?q="+landsearch,urlid, nimed,writer);
        }
        int basiccount = 0;
        HashMap<Character, Integer> landid=new HashMap<>();
        int hulk=basiclands/(colors.length());
        int jääk=basiclands%(colors.length());
        for(char de:colors.toCharArray()){
            if(jääk>0){
            landid.put(de,hulk+1);
            jääk-=1;}
            else{
                landid.put(de,hulk);
            }
        }
        System.out.println("aa");
            if(landid.containsKey('W')){
                Thread.sleep(50);
                writer.write("Plains"+"\n");
                basiccount++;
                request("https://api.scryfall.com/cards/random?q=Plains+-type%3Asnow+%28type%3Abasic+type%3APlains%29+%28game%3Apaper%29",urlid,nimed,writer);
                for (int i = 0; i < landid.get('W')-1; i++) {
                    urlid.add(urlid.getLast());
                    nimed.add(nimed.getLast());
                }
            }
            if(landid.containsKey('U')){
                Thread.sleep(50);
                writer.write("Island"+"\n");
                basiccount++;
                request("https://api.scryfall.com/cards/random?q=Island+-type%3Asnow+%28type%3Abasic+type%3AIsland%29+%28game%3Apaper%29",urlid,nimed,writer);
                for (int i = 0; i < landid.get('U')-1; i++) {
                    urlid.add(urlid.getLast());
                    nimed.add(nimed.getLast());
                }}

            if(landid.containsKey('B')){
                Thread.sleep(50);
                writer.write("Swamp"+"\n");
                basiccount++;
                request("https://api.scryfall.com/cards/random?q=Swamp+-type%3Asnow+%28type%3Abasic+type%3ASwamp%29+%28game%3Apaper%29",urlid,nimed,writer);
                for (int i = 0; i < landid.get('B')-1; i++) {
                    urlid.add(urlid.getLast());
                    nimed.add(nimed.getLast());
                }
            }
            if(landid.containsKey('R')){
                Thread.sleep(50);
                writer.write("Mountain"+"\n");
                basiccount++;
                request("https://api.scryfall.com/cards/random?q=Mountain+-type%3Asnow+%28type%3Abasic+type%3AMountain%29+%28game%3Apaper%29",urlid,nimed,writer);
                for (int i = 0; i < landid.get('R')-1; i++) {
                    urlid.add(urlid.getLast());
                    nimed.add(nimed.getLast());
                }
            }
            if(landid.containsKey('G')){
                Thread.sleep(50);
                writer.write("Forest"+"\n");
                basiccount++;
                request("https://api.scryfall.com/cards/random?q=Forest+-type%3Asnow+%28type%3Abasic+type%3AForest%29+%28game%3Apaper%29",urlid,nimed,writer);
                for (int i = 0; i < landid.get('G')-1; i++) {
                    urlid.add(urlid.getLast());
                    nimed.add(nimed.getLast());
                }
            }

        String ttsfile= ("{\"ObjectStates\":[{\"Name\":\"DeckCustom\",\"ContainedObjects\":[");  ////////hakkab kirjutama tts json filei

        int i = 0;
        while (i<nimed.size()){ ////esimene osa kaartidest
            ttsfile=(ttsfile+"{\"CardID\":"+((i+1)*100)+",\"Name\":\"Card\",\"Nickname\":\""+nimed.get(i)+"\",\"Transform\":{\"posX\":0,\"posY\":0,\"posZ\":0,\"rotX\":0,\"rotY\":180,\"rotZ\":180,\"scaleX\":1,\"scaleY\":1,\"scaleZ\":1}}");
            i++;
            if(i<nimed.size()){
                ttsfile = ttsfile+",";
            }
        }
        ttsfile = ttsfile+"],\"DeckIDs\":[";
        i =0;
        while (i<nimed.size()){ ////esimene osa kaartidest
            ttsfile=ttsfile+((i+1)*100);
            i++;
            if(i<nimed.size()){
                ttsfile = ttsfile+",";
            }
        }
        ttsfile = ttsfile +("],\"CustomDeck\":{");

        i = 0;
        while (i<urlid.size()){ ////esimene osa kaartidest
            String cardface = urlid.get(i);

            ttsfile=ttsfile+("\""+(i+1)+"\":{\"FaceURL\":\""+cardface+"\",\"BackURL\":\"https://i.redd.it/qnnotlcehu731.jpg\",\"NumHeight\":1,\"NumWidth\":1,\"BackIsHidden\":true}");
            i++;
            if(i<nimed.size()){
                ttsfile = ttsfile+",";
            }

        }
        ttsfile = ttsfile+"},\"Transform\":{\"posX\":0,\"posY\":1,\"posZ\":0,\"rotX\":0,\"rotY\":180,\"rotZ\":0,\"scaleX\":1,\"scaleY\":1,\"scaleZ\":1}}";

        ttsfile = ttsfile + "]}";

        try{
            FileWriter wwriter = new FileWriter(new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+"/My Games/Tabletop Simulator/Saves/Saved Objects/deck.json");
            wwriter.write(ttsfile);
            wwriter.close();
        }catch(Exception e){
            FileWriter wwriter = new FileWriter(System.getProperty("user.dir")+"/OneDrive/Documents/My Games/Tabletop Simulator/Saves/Saved Objects/deck.json");
            wwriter.write(ttsfile);
            wwriter.close();
        }

        writer.close();
        s.nextInt();
    }
}
