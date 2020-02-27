package faction;

public enum ResearchKnowledgeSource {

	NONE("UNAVAILABLE"),
	BANK("bank"),
	BOWL3("bowl 3 free action"),
	BOWL2("burning power for bowl 3 free action"),
	NEVLASSPEC3(Nevlas.NAME + " special action"),
	NEVLASSPEC2(Nevlas.NAME + " burning power for special action"),
	HHSPEC(HadschHallas.NAME + " spending credits for knowledge special action"),
	MADDROIDSSPEC(MadAndroids.NAME + " using special action");
	
	private String description;
	
	private ResearchKnowledgeSource(String description) {
		this.description = description;
	}
	
	public String toString() {
		return description;
	}
}
