package cpabe.cpabe;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import cpabe.bswabe.Bswabe;
import cpabe.bswabe.BswabeCph;
import cpabe.bswabe.BswabeCphKey;
import cpabe.bswabe.BswabeElementBoolean;
import cpabe.bswabe.BswabeMsk;
import cpabe.bswabe.BswabePrv;
import cpabe.bswabe.BswabePub;
import cpabe.bswabe.SerializeUtils;
import cpabe.cpabe.policy.LangPolicy;
import it.unisa.dia.gas.jpbc.Element;

public class Cpabe {

	/**
	 * @param args
	 * @author Junwei Wang(wakemecn@gmail.com)
	 */
	
	/**
	 * Setup for the protocol. This function creates a public key and a master key
	 * and stores them into the files specified. The files are stored in your 
	 * workspace directory.
	 * @param pubfile The filename for public key
	 * @param mskfile The filename for master key
	 * @throws IOException if I/O error occurs
	 * @throws ClassNotFoundException 
	 */
	public void setup(String pubfile, String mskfile) throws IOException,
			ClassNotFoundException {
		byte[] pub_byte, msk_byte;
		BswabePub pub = new BswabePub();
		BswabeMsk msk = new BswabeMsk();
		Bswabe.setup(pub, msk);

		/* store BswabePub into mskfile */
		pub_byte = SerializeUtils.serializeBswabePub(pub);
		Common.spitFile(pubfile, pub_byte);

		/* store BswabeMsk into mskfile */
		msk_byte = SerializeUtils.serializeBswabeMsk(msk);
		Common.spitFile(mskfile, msk_byte);
	}
	
	/**
	 * Generate a private key for a user with the specified on the attribute set.
	 * The private key is stored in your workspace default's directory.
	 * @param pubfile The filename public key which has to be used.
	 * @param prvfile The filename of the newly generated private key.
	 * @param mskfile The filename master key which has to be used.
	 * @param attr_str The set of attributes specified as a string. 
	 * Each attribute is separated by a space character and must 
	 * contain a ':' character. The attribute has the following form: 'Attribute:value'
	 * @throws NoSuchAlgorithmException
	 * @throws IOException When the public or master key file cannot be found.
	 */
	public void keygen(String pubfile, String prvfile, String mskfile,
			String attr_str) throws NoSuchAlgorithmException, IOException {
		BswabePub pub;
		BswabeMsk msk;
		byte[] pub_byte, msk_byte, prv_byte;

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		/* get BswabeMsk from mskfile */
		msk_byte = Common.suckFile(mskfile);
		msk = SerializeUtils.unserializeBswabeMsk(pub, msk_byte);

		String[] attr_arr = LangPolicy.parseAttribute(attr_str);
		BswabePrv prv = Bswabe.keygen(pub, msk, attr_arr);

		/* store BswabePrv into prvfile */
		prv_byte = SerializeUtils.serializeBswabePrv(prv);
		Common.spitFile(prvfile, prv_byte);
	}

	/**
	 * Encrypts a file using the specified public key and policy. Outputs the 
	 * encrypted file in your workspace named encfile.
	 * @param pubfile The public key
	 * @param policy The policy to be used. This defines which attributes
	 * are required to decrypt the file. The policy is defined as a string.
	 * An example of the policy: (uid:student2 AND (sn:student2 OR cn:student2)) =
	 * "uid:student2 sn:student2 cn:student2 1of2 2of2"
	 * @param inputfile The file which has to be encrypted
	 * @param encfile The output file, stored in your workspace.
	 * @throws Exception
	 */
	public void enc(String pubfile, String policy, String inputfile,
			String encfile) throws Exception {
		BswabePub pub;
		BswabeCph cph;
		BswabeCphKey keyCph;
		byte[] plt;
		byte[] cphBuf;
		byte[] aesBuf;
		byte[] pub_byte;
		Element m;

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		keyCph = Bswabe.enc(pub, policy);
		cph = keyCph.cph;
		m = keyCph.key;
		System.err.println("m = " + m.toString());

		if (cph == null) {
			System.out.println("Error happed in enc");
			System.exit(0);
		}

		cphBuf = SerializeUtils.bswabeCphSerialize(cph);

		/* read file to encrypted */
		plt = Common.suckFile(inputfile);
		aesBuf = AESCoder.encrypt(m.toBytes(), plt);
		// PrintArr("element: ", m.toBytes());
		Common.writeCpabeFile(encfile, cphBuf, aesBuf);
	}
	
	/**
	 * Tries to decrypt the encfile using the privfile as key. The result
	 * is stored in the decfile in your workspace.
	 * @param pubfile The public key file
	 * @param prvfile The private key file
	 * @param encfile The encrypted message file
	 * @param decfile The name to store the decrypted file
	 * @throws Exception
	 */
	public void dec(String pubfile, String prvfile, String encfile,
			String decfile) throws Exception {
		byte[] aesBuf, cphBuf;
		byte[] plt;
		byte[] prv_byte;
		byte[] pub_byte;
		byte[][] tmp;
		BswabeCph cph;
		BswabePrv prv;
		BswabePub pub;

		/* get BswabePub from pubfile */
		pub_byte = Common.suckFile(pubfile);
		pub = SerializeUtils.unserializeBswabePub(pub_byte);

		/* read ciphertext */
		tmp = Common.readCpabeFile(encfile);
		aesBuf = tmp[0];
		cphBuf = tmp[1];
		cph = SerializeUtils.bswabeCphUnserialize(pub, cphBuf);

		/* get BswabePrv form prvfile */
		prv_byte = Common.suckFile(prvfile);
		prv = SerializeUtils.unserializeBswabePrv(pub, prv_byte);

		BswabeElementBoolean beb = Bswabe.dec(pub, prv, cph);
		System.err.println("e = " + beb.e.toString());
		if (beb.b) {
			plt = AESCoder.decrypt(beb.e.toBytes(), aesBuf);
			Common.spitFile(decfile, plt);
		} else {
			System.exit(0);
		}
	}

}