package wealthDistribution;

public class Land {
	private int grainHere;
	private int maxGrainHere;
	private int landxLocation;
	private int landyLocation;
	public Land(int grainHere,int maxGrainHere,int landxLocation,int landyLocation){
		this.setGrainHere(grainHere);
		this.setMaxGrainHere(maxGrainHere);
		this.setLandxLocation(landxLocation);
		this.setLandyLocation(landyLocation);
	}
	public int getGrainHere() {
		return grainHere;
	}
	public void setGrainHere(int d) {
		this.grainHere = d;
	}
	public int getMaxGrainHere() {
		return maxGrainHere;
	}
	public void setMaxGrainHere(int maxGrainHere) {
		this.maxGrainHere = maxGrainHere;
	}
	public int getLandxLocation() {
		return landxLocation;
	}
	public void setLandxLocation(int landxLocation) {
		this.landxLocation = landxLocation;
	}
	public int getLandyLocation() {
		return landyLocation;
	}
	public void setLandyLocation(int landyLocation) {
		this.landyLocation = landyLocation;
	}
	
}