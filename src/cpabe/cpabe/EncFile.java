package cpabe.cpabe;

public class EncFile {
	
	private byte[] aesBuf;
	private byte[] cphBuf;
	
	public EncFile(byte[] aesBuf, byte[] cphBuf) {
		this.aesBuf = aesBuf;
		this.cphBuf = cphBuf;
	}
	
	public byte[] getAesBuf() {
		return aesBuf;
	}
	
	public byte[] getCphBuf() {
		return cphBuf;
	}

}
