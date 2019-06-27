# phenix-challenge

build projet en utilisant la commande maven:
    
	mvn clean compile assembly:single

run projet:
	
	java -Xmx512m -jar target/phenix-challenge-1.0-SNAPSHOT-jar-with-dependencies.jar /home/aymen/data /home/aymen/temp home/aymen/output 20190629


#Arguments:
	
	/home/aymen/data: dossier qui contient les fichiers réferentiels sous nom reference_prod-ID_MAGASIN_YYYYMMDD.data et les fichiers transactions sous nom transactions_YYYYMMDD.data

	/home/aymen/temp: dossier pour les fichies de résultats intermédiaires doit être initialement vide pour chaque éxecution de programme

	/home/aymen/output: contient les fichiers résultats  

	20190629: date de jour qu'on va calculer ses indicateurs et en considérant les 7 derniers jours avant cette date   


#Algorithme:

Date de jour passé en arguments = 20190629

1- Calculer les 100 meilleures ventes/magasin/jour:

	- Récupperer tous les ids magasins à partir des noms des fichiers réferentiels en utilisant une expression régulière

	- Partitionner le fichier des transactions de jour passé en argument par id magasin en stockant ces fichiers sous nom
	  transactions_ID_MAGASIN_20190629.data dans temp/transactions/

	- Pour chaque fichier transactions_ID_MAGASIN_20190629.data on fait regroupement par produit et en faisant la somme de
	  quantité 

	- stocker les résultats de calcul dans output/ sous format top_100_ventes_<ID_MAGASIN>_YYYYMMDD.data   


2- Calculer les 100 meilleures CA/magasin sur les 7 derniers jours:

	- calculer les 7 derniers jours avant 20190629

	- Récupperer tous les ids magasins à partir des noms des fichiers réferentiels en utilisant une expression régulière

	- Partitionner les fichiers des transactions de 7 derniers jour par id magasin en stockant ces fichiers sous nom 
	  transactions_ID_MAGASIN_20190629.data dans temp/transactions/

	- On fait une jointure entre le ficher transaction par id Magasin et le réferentiel correspendant pour calculer le pix de
	  chaque transaction on stocke le resultat dans temp/mappedTransactions/

	- on fait le merge des fichiers de même id magasin pour obtenir un fichier temp/merge/
	  transactions_ID_MAGASIN_20190629.data.merge qui contient tous les transactions de magasin sur 7 jours

	- regrouper chaque fichier temp/merge/transactions_ID_MAGASIN_20190629.data.merge par produit en faisant la somme de 
	  prix on stocke le resultat dans output sous le nom top_100_ca_ID_Magasin_yyyyMMdd-J7.data       



