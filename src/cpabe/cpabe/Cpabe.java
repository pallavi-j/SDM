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
import phrApp.DatabaseHandler;

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
	 * Takes a public key and data about a PHR and encrypts the data using the specified policy.
	 * The data is stored in the database.
	 * @param pubfile The location of the public key
	 * @param policy The policy which is needed to decrypt the file, e.g. 'pID:04 dID:12 1of2'
	 * @param input The PHR details specified as a string.
	 * @throws Exception
	 */
	//TODO: Store the data in the table public_health_record instead of 'Temp'.
	public void enc(String pubfile, String policy, String input) throws Exception {
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
		String inputString = input;
		plt = inputString.getBytes();
		aesBuf = AESCoder.encrypt(m.toBytes(), plt);
		// PrintArr("element: ", m.toBytes());		
		DatabaseHandler.authentication("127.0.0.1", 3306, "richard", "12345");
		DatabaseHandler.addTemp(aesBuf, cphBuf);
	}
	
	public EncFile enc2(String pubfile, String policy, String input) throws Exception {
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
		String inputString = input;
		plt = inputString.getBytes();
		aesBuf = AESCoder.encrypt(m.toBytes(), plt);
		// PrintArr("element: ", m.toBytes());
		return new EncFile(aesBuf, cphBuf);
	}
	
	/**
	 * Takes the public key and the private key of a specific user and tries to decrypt a public health record.
	 * Return a string containing either the public health record data or an error message.
	 * @param pubfile The location of the public key file
	 * @param prvfile The location of the private key file
	 * @param PHRID The public health record ID which as to be decrpyted
	 * @throws Exception
	 */
	//TODO: currently uses a temporary table named 'Temp', this should be changed to 'patient_health_record'
	public String dec(String pubfile, String prvfile, int PHRID) throws Exception {
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
		DatabaseHandler.authentication("127.0.0.1", 3306, "richard", "12345");
		aesBuf = DatabaseHandler.searchForaesBufByPHRID(PHRID);
		cphBuf = DatabaseHandler.searchForcphBufByPHRID(PHRID);
		
		cph = SerializeUtils.bswabeCphUnserialize(pub, cphBuf);

		/* get BswabePrv form prvfile */
		prv_byte = Common.suckFile(prvfile);
		prv = SerializeUtils.unserializeBswabePrv(pub, prv_byte);

		BswabeElementBoolean beb = Bswabe.dec(pub, prv, cph);
		System.err.println("e = " + beb.e.toString());
		if (beb.b) {
			plt = AESCoder.decrypt(beb.e.toBytes(), aesBuf);
			//Common.spitFile(decfile, plt); //skip, instead show the output of decreption on screen!
			System.out.println(new String(plt));
			return new String(plt);
		} else {
			return new String("You do not have access to this file");
		}
	}
	
	public String dec(String pubfile, String prvfile, byte[] aesBuf, byte[] cphBuf) throws Exception {
		//byte[] aesBuf, cphBuf;
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
		//DatabaseHandler.authentication("127.0.0.1", 3306, "richard", "12345");
		//aesBuf = DatabaseHandler.searchForaesBufByPHRID(PHRID);
		//cphBuf = DatabaseHandler.searchForcphBufByPHRID(PHRID);
		
		cph = SerializeUtils.bswabeCphUnserialize(pub, cphBuf);

		/* get BswabePrv form prvfile */
		prv_byte = Common.suckFile(prvfile);
		prv = SerializeUtils.unserializeBswabePrv(pub, prv_byte);

		BswabeElementBoolean beb = Bswabe.dec(pub, prv, cph);
		System.err.println("e = " + beb.e.toString());
		if (beb.b) {
			plt = AESCoder.decrypt(beb.e.toBytes(), aesBuf);
			//Common.spitFile(decfile, plt); //skip, instead show the output of decreption on screen!
			System.out.println(new String(plt));
			return new String(plt);
		} else {
			return new String("You do not have access to this file");
		}
	}

}