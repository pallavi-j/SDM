package phrApp;

public class PHR {
	private int id;
	private byte[] detail;
	private String policy;
	private int ownerID;
	private int authorID;
	private int doctorID;
	private int insuranceID;
	
	public PHR(int id, byte[] detail, String policy, int ownerID, int authorID, int doctorID, int insuranceID) {
		this.setId(id);
		this.setDetail(detail);
		this.setPolicy(policy);
		this.setOwnerID(ownerID);
		this.setAuthorID(authorID);
		this.setDoctorID(doctorID);
		this.setInsuranceID(insuranceID);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getDetail() {
		return detail;
	}

	public void setDetail(byte[] detail) {
		this.detail = detail;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public int getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}

	public int getAuthorID() {
		return authorID;
	}

	public void setAuthorID(int authorID) {
		this.authorID = authorID;
	}

	public int getDoctorID() {
		return doctorID;
	}

	public void setDoctorID(int doctorID) {
		this.doctorID = doctorID;
	}

	public int getInsuranceID() {
		return insuranceID;
	}

	public void setInsuranceID(int insuranceID) {
		this.insuranceID = insuranceID;
	}

}
