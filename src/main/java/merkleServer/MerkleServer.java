package merkleServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import merkleClient.HashUtil;

public class MerkleServer {

    public static final String END_OF_SESSION = "close";

    @SuppressWarnings("unused")
    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(1111);

        try{
            while(true){
                Socket client;
                System.out.println("Waiting for connection...");
                client = listener.accept();
                System.out.println("Connected, waiting for nodes..");
                try{
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));


                    String nodeToBeChecked = in.readLine();
                    System.out.println("Recived node: " + nodeToBeChecked);

                    /*

                       **Calculating the nodes to send back;**

                       This part will not be developed, we just assume it will be done, sending back a list of custom
                       nodes.
                     */

                    PrintStream out = new PrintStream(client.getOutputStream(), true);

                    if(nodeToBeChecked != null) {
                        out.println(HashUtil.md5Java("3"));
                        out.println(HashUtil.md5Java("4567"));
                        out.println(HashUtil.md5Java("12"));
                        out.println("done");

                        out.flush();
                    }

                }catch(IOException e){
                    System.out.println("Error");
                }
            }
        }catch(IOException e){
            System.out.println("Error");
        }
    }
}
