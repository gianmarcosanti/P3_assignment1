package merkleClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class MerkleValidityRequest {

	/**
	 * IP address of the authority
	 * */
	private final String authIPAddr;
	/**
	 * Port number of the authority
	 * */
	private final int  authPort;
	/**
	 * Hash value of the merkle tree root. 
	 * Known before-hand.
	 * */
	private final String mRoot;
	/**
	 * List of transactions this client wants to verify 
	 * the existence of.
	 * */
	private List<String> mRequests;
	
	/**
	 * Sole constructor of this class - marked private.
	 * */
	private MerkleValidityRequest(Builder b){
		this.authIPAddr = b.authIPAddr;
		this.authPort = b.authPort;
		this.mRoot = b.mRoot;
		this.mRequests = b.mRequest;
	}
	
	/**
	 * <p>Method implementing the communication protocol between the client and the authority.</p>
	 * <p>The steps involved are as follows:</p>
	 * 		<p>0. Opens a connection with the authority</p>
	 * 	<p>For each transaction the client does the following:</p>
	 * 		<p>1.: asks for a validityProof for the current transaction</p>
	 * 		<p>2.: listens for a list of hashes which constitute the merkle nodes contents</p>
	 * 	<p>Uses the utility method {@link #) isTransactionValid} </p>
	 * 	<p>method to check whether the current transaction is valid or not.</p>
	 * */
	public Map<Boolean, List<String>> checkWhichTransactionValid() throws IOException {

		Map<Boolean, List<String>> checkedTransactions = new HashMap<>();
		checkedTransactions.put(true, new ArrayList<>());
		checkedTransactions.put(false, new ArrayList<>());


		try {

			/*
			 *
			 * Chiedo all'authority la lista di nodi per verificare ogni transazione, richiamando un opportuna funzione
			 * in modo da rendere il codice più leggibile. Una volta ricevuta la lista dei nodi procedo con il calcolo
			 * della root per verificare la tranzazione
			 *
			 */

			mRequests.stream().forEach( request-> {
                Socket authoritySocket = null;

                try {

                    /*
	            		Open connection to server
	               	 */

                    authoritySocket = new Socket(authIPAddr, authPort);

                    /*
                     * I had troubles with the multiple connections and I found that the only way to make multiple
                     * communications was open every time a new connection.
                     */

                    List<String> mNodes = getNodesFromServer(authoritySocket, request);
                    boolean validityProof = isTransactionValid(request, mNodes);
                    checkedTransactions.get(validityProof).add(request);

                     /*
	            		Close connection to server
	               	 */

                    authoritySocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

			});

		}catch (Exception e){
            System.out.println("errore");
		}

		return checkedTransactions;
	}
	
	/**
	 * 	Checks whether a transaction 'merkleTx' is part of the merkle tree.
	 * 
	 *  @param merkleTx String: the transaction we want to validate
	 *  @param merkleNodes String: the hash codes of the merkle nodes required to compute 
	 *  the merkle root
	 *  
	 *  @return: boolean value indicating whether this transaction was validated or not.
	 * */

	private boolean isTransactionValid(String merkleTx, List<String> merkleNodes) {

		String hashedMarkleTx = HashUtil.md5Java(merkleTx);

		AtomicReference<String> root = new AtomicReference<>(HashUtil.md5Java((hashedMarkleTx + merkleNodes.get(0))));
		merkleNodes.remove(0);

		merkleNodes.stream().forEachOrdered(x -> root.set(HashUtil.md5Java(root + x)));

		return  mRoot.equals(root);
	}

	/**
	 *
	 * Funzione che riceve come parametri un request (-> Transazione hashata) e restituisce una lista di nodi utili a
	 * calcolare la radice del merkleTree.
	 * @param request String: the transaction we want to validate.
	 * @param authoritySocket Socket: Socket needed to communicate with the autority.
	 *
	 * @return nodes List<String> : List of hashed nodes the caller needs to compute merkletree root
	 */

	private List<String> getNodesFromServer (Socket authoritySocket, String request) {

		List<String> nodeToCheckTransaction =  new ArrayList<String>();

		try {
            PrintStream out = new PrintStream(authoritySocket.getOutputStream(), true );

			out.println(request);


			System.out.println("Just send the transaction: "+request);


            BufferedReader in = new BufferedReader(new InputStreamReader(authoritySocket.getInputStream()));

			String incomingNodes = in.readLine();
			while(incomingNodes != null && !incomingNodes.equals("done")) {
				nodeToCheckTransaction.add(incomingNodes);
				incomingNodes = in.readLine();

			}


		} catch (IOException e) {
			System.out.println("The socket for reading the object has problem");
			e.printStackTrace();
		}

		return nodeToCheckTransaction;
	}


		/**
         * Builder for the MerkleValidityRequest class.
         * */

	public static class Builder {
		private String authIPAddr;
		private int authPort;
		private String mRoot;
		private List<String> mRequest;	
		
		public Builder(String authorityIPAddr, int authorityPort, String merkleRoot) {
			this.authIPAddr = authorityIPAddr;
			this.authPort = authorityPort;
			this.mRoot = merkleRoot;
			mRequest = new ArrayList<>();
		}
				
		public Builder addMerkleValidityCheck(String merkleHash) {
			mRequest.add(merkleHash);
			return this;
		}
		
		public MerkleValidityRequest build() {
			return new MerkleValidityRequest(this);
		}
	}
}