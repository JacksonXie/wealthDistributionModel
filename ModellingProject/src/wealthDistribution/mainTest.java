package wealthDistribution;
//TODO maxWealth for social class
//TODO TEST
public class mainTest {
	//lorenz-and-gini
	public static int totalWealth;
	public static int maxWealth;
	//parameters of coordinate
    public static int xLocationMax = 10;
    public static int yLocationMax = 10;
    public static int ticks = 10;
 
	//parameters of people 
	public static int lifeExpectancyMax = 100;
	public static int lifeExpectancyMin = 50;
	public static int metabolismMax = 100;
	public static int maxVision = 4;
	public static int peopleNumber = 3;
	//parameters of land
	public static int maxGrain = 50;
	public static double percentBestLand = 20.0;
	public static int grainGrowthInterval = 1;
	public static int numGrainGrow = 5;
	
	public static void main(String[] args) {
		 Land[][] landArray = new Land[yLocationMax][xLocationMax];
		 Land[][] futureLandArray = new Land[yLocationMax][xLocationMax];
		 Person[] personArray = new Person[peopleNumber];
		 initializePeople(personArray);
		 initializeLand(landArray,futureLandArray);
		 for(int N=ticks;N>=0;N--){
			 runTheSystem(personArray,landArray,futureLandArray,N);
			 System.out.println("N:"+N);
			 for (int i=0;i<personArray.length;i++){
				 System.out.println(Person.toString(personArray[i]));
			 }
		 }
	}
	private static void initializePeople(Person[] personArray){
		for (int i=0;i<personArray.length;i++){
			 initializeAPerson(personArray,i);
			 int wealth = personArray[i].getWealth();
			 totalWealth= totalWealth +wealth;
			 if(wealth>maxWealth){
				 maxWealth = wealth;
			 }
		}
		for(int i=0;i<personArray.length;i++){
			int wealth = personArray[i].getWealth();
			int socialClass = getSocialClass(maxWealth,wealth);
			
			personArray[i].setSocialClass(socialClass);
		}
	}
	private static void initializeAPerson(Person[] personArray, int i){
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
	private static int getSocialClass(int maxWealth,int wealth){
		int socialClass = 0;
		if(wealth<(1/3)*maxWealth){
			socialClass = 1;
		}else if(wealth<(2/3)*maxWealth){
			socialClass = 2;
		}else if(wealth>=(2/3)*maxWealth){
			socialClass = 3;
			System.out.println("check class");
		}else{
			socialClass = 0;
		}
		return socialClass;
	}
	private static void initializeLand(Land[][] landArray, Land[][] futureLandArray){
		int eachNeighborGetFive = (int)Math.floor(5*maxGrain*0.25*0.125);
		// set the best land
		for (int i=0;i<landArray.length;i++)
			for(int j=0;j<landArray[i].length;j++){
				int grainHere = Calculator.getGrainHere(percentBestLand,maxGrain);
				landArray[i][j] = new Land(grainHere,grainHere,j,i,0);
			}
		// initialize future land
		for (int i=0;i<landArray.length;i++)
			for(int j=0;j<landArray[i].length;j++){
				futureLandArray[i][j] = new Land(0,0,j,i,0);
			}
		//diffuse repeat 5 times
		for (int i=0;i<landArray.length;i++)
			for(int j=0;j<landArray[i].length;j++){
				if(landArray[i][j].getMaxGrainHere()!=0){
					diffuseGrain(landArray,futureLandArray,i,j,eachNeighborGetFive);
				}
			}
		//diffuse repeat 10 times
		for(int N=0;N<10;N++){
			for (int i=0;i<landArray.length;i++)
				for(int j=0;j<landArray[i].length;j++){
					diffuseGrain(landArray,futureLandArray,i,j);
				}
			//reset current land's grain
			for (int i=0;i<landArray.length;i++)
				for(int j=0;j<landArray[i].length;j++){
					landArray[i][j].setGrainHere(futureLandArray[i][j].getGrainHere());
				}
		}	
	}	
	private static void diffuseGrain(Land[][] landArray,Land[][] futureLandArray,int i,int j,int eachNeighborGetFive ){
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
	
	private static void diffuseGrain(Land[][] landArray,Land[][] futureLandArray,int x,int y){
		for(int m=y-1;m<=y+1;m++)
			for(int n=x-1;n<=x+1;n++){
				if(m>=0 && n>=0 && m<xLocationMax && n<yLocationMax){
					int currentGrain = landArray[m][n].getGrainHere();
					if(m==y&&n==x){
						int restGrain = (int) (currentGrain*0.75);
						futureLandArray[m][n].setGrainHere(restGrain);
					}else{
						int diffuseGrain = (int)(currentGrain*0.25*0.125);
						futureLandArray[m][n].setGrainHere(diffuseGrain+currentGrain);
					}
				}
			}
	}
	private static void runTheSystem(Person[] personArray,Land[][] landArray,Land[][] futureLandArray,int tick){
		//turn-towards-grain
		turnTowardsGrain(personArray,landArray);
		moveEatAgeDie(personArray,landArray,futureLandArray);
		staticEachLandPeopleNumber(landArray,personArray);
		growGrain(landArray,futureLandArray,tick);
	}
	private static void turnTowardsGrain(Person[] personArray,Land[][] landArray){
		int nextHeadDirection = 0;
		int currentMaxGrain = 0;
		int[]nextMoveLocation = new int[2];
		for (int i=0;i<personArray.length;i++){
			int xLocation = personArray[i].getxLocation();
			int yLocation = personArray[i].getyLocation();
			int HeadDirection = personArray[i].getHeadDirection();
			for(int t=0;t<4;t++){
				int currentHeadDirection = (HeadDirection+t)%4;//change Direction each time;
				switch(currentHeadDirection) {
			    	case 0: nextMoveLocation = checkHeadDirection(maxVision,landArray,xLocation,yLocation,0,-1);
			    			break;
			    	case 1: nextMoveLocation = checkHeadDirection(maxVision,landArray,xLocation,yLocation,1,0);
	    					break;
			    	case 2: nextMoveLocation = checkHeadDirection(maxVision,landArray,xLocation,yLocation,0,1);
	    					break;
			    	case 3: nextMoveLocation = checkHeadDirection(maxVision,landArray,xLocation,yLocation,-1,0);
	    					break;
	    			default:nextMoveLocation = null;
				}
				int currentGrain = landArray[nextMoveLocation[1]][nextMoveLocation[0]].getGrainHere();
				if(currentGrain>currentMaxGrain){
					nextHeadDirection = currentHeadDirection;
					currentMaxGrain = currentGrain;
				}
			}
			personArray[i].setHeadDirection(nextHeadDirection);
			personArray[i].setNextXLocation(nextMoveLocation[0]);
			personArray[i].setNextYLocation(nextMoveLocation[1]);
		}
	}
	private static int[] checkHeadDirection(int maxVision,Land[][] landArray,int x,int y,int addx,int addy){
		int maxGrain = 0;
		int[]nextMoveLocation  = new int[2];
		for(int i=1;i<=maxVision;i++){
			int currentX = x+i*addx;
			int currentY = y+i*addy;
			if(!(currentX>=0&&currentY>=0&&currentX<xLocationMax&&currentY<yLocationMax)){
				if(currentX<0){
					currentX = x-i*addx;//back to previous location
					currentX = xLocationMax-1-(maxVision-i);
				}else if(currentX>=xLocationMax){
					currentX = x-i*addx;
					currentX = maxVision-i;
				}else if(currentY<0){
					currentY = y-i*addy;
					currentY = yLocationMax-1-(maxVision-i);
				}else if(currentY>=yLocationMax){
					currentY = y-i*addy;
					currentY = maxVision-i;
				}else{
					System.out.println("checkHeadWrong");
				}
			}
			int currentGrain = landArray[currentY][currentX].getGrainHere();
			if(currentGrain>maxGrain){
				maxGrain = currentGrain;
				nextMoveLocation[0]=currentX;
				nextMoveLocation[1]=currentY;
			}
		}
		return nextMoveLocation;
	}
	private static void moveEatAgeDie(Person[] personArray,Land[][] landArray,Land[][] futureLandArray){
		for (int i=0;i<personArray.length;i++){
			// calculate the values of people
			int metabolism = personArray[i].getMetabolism();
			int currentWealth = personArray[i].getWealth()-metabolism;
			int currentAge = personArray[i].getAge()+1;
			int lifeExpectancy = personArray[i].getLifeExpectancy();
			//if person die, then random create a new person.else move to a new land
			if(currentWealth<0||currentAge>=lifeExpectancy){
				initializeAPerson(personArray,i);
				int wealth = personArray[i].getWealth();
				totalWealth= totalWealth +wealth;
				if(wealth>maxWealth){
					 maxWealth = wealth;
				}
				int socialClass = getSocialClass(maxWealth,wealth);
				personArray[i].setSocialClass(socialClass);
			}else{
				int currentXLocation = personArray[i].getxLocation();
				int currentYLocation = personArray[i].getyLocation();
				int nextXLocation = personArray[i].getNextXLocation();
				int nextYLocation = personArray[i].getNextYLocation();
				personArray[i].setxLocation(nextXLocation);
				personArray[i].setyLocation(nextYLocation);
				int landCurrentPeopleNumber = landArray[currentYLocation][currentXLocation].getLandPeopleNumber();
				landArray[currentYLocation][currentXLocation].setLandPeopleNumber(landCurrentPeopleNumber-1);
				int nextLandPeopleNumber = landArray[nextYLocation][nextXLocation].getLandPeopleNumber();
				landArray[nextYLocation][nextXLocation].setLandPeopleNumber(nextLandPeopleNumber+1);
				personArray[i].setAge(currentAge);
				personArray[i].setWealth(currentWealth);
				int socialClass = getSocialClass(maxWealth,currentWealth);
				personArray[i].setSocialClass(socialClass);
			}
		}
		// static people on lands
		staticEachLandPeopleNumber(landArray,personArray);
		//harvest
		for (int i=0;i<personArray.length;i++){
			int currentXLocation = personArray[i].getxLocation();
			int currentYLocation = personArray[i].getyLocation();
			int grainHere = landArray[currentYLocation][currentXLocation].getGrainHere();
			int peopleHere = landArray[currentYLocation][currentXLocation].getLandPeopleNumber();
			int currentWealth = personArray[i].getWealth();
			int wealth = 0;
			if(peopleHere!=0){
				wealth = (int)Math.floor(currentWealth + grainHere/peopleHere);
			}
			personArray[i].setWealth(wealth);
			//mark grain here zero
			futureLandArray[currentYLocation][currentXLocation].setGrainHere(0);
		}
		//set Grain Here zero
		for (int i=0;i<landArray.length;i++)
			for(int j=0;j<landArray[i].length;j++){
				int grainHere = futureLandArray[i][j].getGrainHere();
				landArray[i][j].setGrainHere(grainHere);
			}
	}
	private static void staticEachLandPeopleNumber(Land[][] landArray,Person[] personArray){
		for (int i=0;i<personArray.length;i++){
			int xLocation = personArray[i].getxLocation();
			int yLocation = personArray[i].getyLocation();
			int currentPeople = landArray[yLocation][xLocation].getLandPeopleNumber();
			landArray[yLocation][xLocation].setLandPeopleNumber(currentPeople+1);
		}
	}
	private static void growGrain(Land[][] landArray,Land[][] futureLandArray,int tick){
		if(tick%grainGrowthInterval==0){
			for (int i=0;i<futureLandArray.length;i++)
				for(int j=0;j<futureLandArray[i].length;j++){
					int grainHere = landArray[i][j].getGrainHere();
					int grainMaxHere = landArray[i][j].getMaxGrainHere();
					if(grainHere<grainMaxHere){
						grainHere+=numGrainGrow;
						if(grainHere>grainMaxHere){
							grainHere = grainMaxHere;
						}
						futureLandArray[i][j].setGrainHere(grainHere);
					}
				}
			for (int i=0;i<landArray.length;i++)
				for(int j=0;j<landArray[i].length;j++){
					int grainHere = futureLandArray[i][j].getGrainHere();
					landArray[i][j].setGrainHere(grainHere);
				}
			}
	}
}