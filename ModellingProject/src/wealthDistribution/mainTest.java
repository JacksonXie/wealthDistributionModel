package wealthDistribution;

public class mainTest {
	//TODO build future Array
	//parameters of coordinate
    public static int xLocationMax = 10;
    public static int yLocationMax = 10;
    public static int ticks = 100;
 
	//parameters of people 
	public static int lifeExpectancyMax = 100;
	public static int lifeExpectancyMin = 50;
	public static int metabolismMax = 100;
	public static int maxVision = 4;
	public static int peopleNumber = 50;
	//parameters of land
	public static int maxGrain = 50;
	public static double percentBestLand = 20.0;
	public static void main(String[] args) {
		 Land[][] landArray = new Land[yLocationMax][xLocationMax];
		 Land[][] futureLandArray = new Land[yLocationMax][xLocationMax];
		 Person[] personArray = new Person[peopleNumber];
		 initializePeople(personArray);
		 initializeLand(landArray,futureLandArray);
		 for (int i=0;i<personArray.length;i++){
			 System.out.println(Person.toString(personArray[i]));
		 }
		 runTheSystem(personArray,landArray,futureLandArray);
	}
	private static void initializePeople(Person[] personArray){
		for (int i=0;i<personArray.length;i++){
			 int lifeExpectancy = Calculator.getLifeExpectancy(lifeExpectancyMax,lifeExpectancyMin);
			 int age = Calculator.getAge(lifeExpectancy);
			 int metabolism = Calculator.getMetabolism(metabolismMax);
			 int vision = Calculator.getVision(maxVision);
			 int xLocation = Calculator.getXLocation(xLocationMax);
			 int yLocation = Calculator.getYLocation(yLocationMax);
     		 int HeadDirection = Calculator.getHeadDirection();
             int wealth = Calculator.getWealth(metabolism);
             int nextXLocation = 0;
             int nextYLocation = 0;
			 personArray[i]  = new Person(age,wealth,lifeExpectancy,metabolism,vision,xLocation,yLocation,HeadDirection,nextXLocation,nextYLocation);
		}
	}
	private static void initializeLand(Land[][] landArray, Land[][] futureLandArray){
		int eachNeighborGetFive = (int)Math.floor(5*maxGrain*0.25*0.125);
		// set the best land
		for (int i=0;i<landArray.length;i++)
			for(int j=0;j<landArray[i].length;j++){
				int grainHere = Calculator.getGrainHere(percentBestLand,maxGrain);
				landArray[i][j] = new Land(grainHere,grainHere,j,i);
			}
		// initialize future land
		for (int i=0;i<landArray.length;i++)
			for(int j=0;j<landArray[i].length;j++){
				futureLandArray[i][j] = new Land(0,0,j,i);
			}
		//diffuse repeat 5 times
		for (int i=0;i<landArray.length;i++)
			for(int j=0;j<landArray[i].length;j++){
				if(landArray[i][j].getMaxGrainHere()!=0){
				for(int m=i-1;m<=i+1;m++)
					for(int n=j-1;n<=j+1;n++){
						if(m>=0 && n>=0 && m<xLocationMax && n<yLocationMax){
							int currentGrain = landArray[m][n].getGrainHere();
							if(m!=i||n!=j){
								landArray[m][n].setGrainHere(currentGrain+eachNeighborGetFive);
							}else{
								landArray[m][n].setGrainHere((int)(0.75*currentGrain));
							}
						}
					}
				}
			}
		//diffuse repeat 10 times
		for(int N=0;N<10;N++){
			for (int i=0;i<landArray.length;i++)
				for(int j=0;j<landArray[i].length;j++){
					for(int m=i-1;m<=i+1;m++)
						for(int n=j-1;n<=j+1;n++){
							if(m>=0 && n>=0 && m<xLocationMax && n<yLocationMax){
								int currentGrain = landArray[m][n].getGrainHere();
								if(m==i&&n==j){
									int restGrain = (int) (currentGrain*0.75);
									futureLandArray[m][n].setGrainHere(restGrain);
								}else{
									int diffuseGrain = (int)(currentGrain*0.25*0.125);
									futureLandArray[m][n].setGrainHere(diffuseGrain+currentGrain);
								}
							}
						}
				}
			//reset current land's grain
			for (int i=0;i<landArray.length;i++)
				for(int j=0;j<landArray[i].length;j++){
					landArray[i][j].setGrainHere(futureLandArray[i][j].getGrainHere());
				}
		}		
	}
	private static void runTheSystem(Person[] personArray,Land[][] landArray,Land[][] futureLandArray){
		//turn-towards-grain
		turnTowardsGrain(personArray,landArray);
		
	}
	private static void turnTowardsGrain(Person[] personArray,Land[][] landArray){
		for (int i=0;i<personArray.length;i++){
			int xLocation = personArray[i].getxLocation();
			int yLocation = personArray[i].getyLocation();
			int HeadDirection = personArray[i].getHeadDirection();
			
			
		}
	}
}