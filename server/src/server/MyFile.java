package server;

public class MyFile {
	private int id;
	private String name;
	private byte[] data;
	private String filExtencion;

	public MyFile(int id, String name, byte[] data, String filExtencion) {
		super();
		this.id = id;
		this.name = name;
		this.data = data;
		this.filExtencion = filExtencion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFilExtencion() {
		return filExtencion;
	}

	public void setFilExtencion(String filExtencion) {
		this.filExtencion = filExtencion;
	}

}
