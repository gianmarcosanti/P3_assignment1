package merkleClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main {

	public static final void main(String args[]) throws IOException {
		
		String merkleRoot = HashUtil.md5Java("01234567");
		String merkleTx_1 = HashUtil.md5Java("2");
		String merkleTx_2 = HashUtil.md5Java("0000000020");
		
		Map<Boolean, List<String>> report = new MerkleValidityRequest.Builder("127.0.0.1", 1111, merkleRoot)
								 									.addMerkleValidityCheck(merkleTx_1)
								 									.addMerkleValidityCheck(merkleTx_2)
								 									.build()
								 									.checkWhichTransactionValid();

		//print the valid transactions.
		report.entrySet().stream()
						 .forEach(System.out::println);


	}	
}
