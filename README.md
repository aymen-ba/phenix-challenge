# phenix-challenge

build project using maven:
    
	mvn clean
	mvn compile
	mvn package
    
run project:

	// without constraints
	java -cp target/phenix-challenge-1.0-SNAPSHOT.jar Main /home/aymen/data /home/aymen/temp /home/aymen/output 20190629

	// constraint: RAM <= 512MO
	java -Xmx512m -cp target/phenix-challenge-1.0-SNAPSHOT.jar Main /home/aymen/data /home/aymen/temp /home/aymen/output 20190629

	// constraint : RAM <= 512MO and 2 CPU
	cpulimit -l 25 java -Xmx512m -cp target/phenix-challenge-1.0-SNAPSHOT.jar Main /home/aymen/data /home/aymen/temp /home/aymen/output 20190629


