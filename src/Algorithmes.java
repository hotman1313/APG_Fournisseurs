import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe contenant les algorithmes
 * 
 * @author othmane
 * @version 1.0
 */

public class Algorithmes {

	/**
	 * Liste de tous les fournisseurs
	 * */
	private List<Fournisseur> fournisseurs;
	
	/**
	 * Sous-ensemble de fournisseurs
	 * */
	private List<Fournisseur> fournisseursRetenus;
	
	/**
	 * Seos-ensemble Y
	 * */
	private Map<Integer, Boolean> clientsY;
	
	/**
	 * Les fournisseurs ouverts et fermés
	 * */
	private Map<Integer, Boolean> fournisseursOuvertsFermes;
	
	/**
	 * Liste des clients (connectés et non connectés)
	 * */
	private Map<Integer, Boolean> lesClients;
	
	/**
	 * Constructeur
	 * */
	public Algorithmes(List<Fournisseur> fournisseurs) {
		this.fournisseurs = fournisseurs;
		this.fournisseursRetenus = new ArrayList<>();
		this.lesClients = new HashMap<>();
		this.fournisseursOuvertsFermes = new HashMap<>();
		this.clientsY = new HashMap<>();
		trierCoutConnexion(getFournisseurs());
		//dans un premier temps, aucun fournisseur connecté
		int i = 0;
		for (int cout : getFournisseurs().get(0).getCoutsClients()) {
			lesClients.put(i, false);
			i++;
		}
		
		//dans un premier temps, aucun fournisseur ouvert
		i = 0;
		for (Fournisseur fournisseur : getFournisseurs()) {
			fournisseursOuvertsFermes.put(i, false);
			i++;
		}
	}
	
	/**
	 * Fonction eval(O)
	 * */
	public int eval(List<Fournisseur> O) {
		int sommeCoutsOuvs = 0;
		int sommeCoutsconnexClis = 0;
		int minCliCourant;		
		
		//somme des coûts d'ouverture des fournisseurs retenu
		for (int i = 0; i < O.size(); i++) {
			sommeCoutsOuvs += O.get(i).getCoutOuverture();
		}
		
		for (int i = 0; i < getLesClients().size(); i++) {
			minCliCourant = Integer.MAX_VALUE;
			for (int j = 0; j < O.size(); j++) {
				if(O.get(j).getCoutsClients().get(i) < minCliCourant) {
					minCliCourant = O.get(j).getCoutsClients().get(i);
				}
			}
			sommeCoutsconnexClis += minCliCourant;
		}
		
		return sommeCoutsOuvs+sommeCoutsconnexClis;
	}
	

	/**
	 * Algorithme 1 : Glouton
	 * */
	public List<Fournisseur> glouton1() {
		//declarations
		List<Fournisseur> iFournisseurs = new ArrayList<>();
		Fournisseur fournisseurTemp = null;
		int iMin = Integer.MAX_VALUE;
		int nbFournisseurs = getFournisseurs().size();
		
		//ajout du premier (par défaut)
		getFournisseursRetenus().add(getFournisseurs().get(0));

		//choix du premier fournisseur moins cher
		for (int i = 1; i < getFournisseurs().size(); i++) {
			iFournisseurs.add(getFournisseurs().get(i));
			if(eval(iFournisseurs) < eval(getFournisseursRetenus())) {
				getFournisseursRetenus().clear();
				getFournisseursRetenus().add(iFournisseurs.get(0));
			}
			iFournisseurs.clear();
		}
		
		iFournisseurs = new ArrayList<>(getFournisseursRetenus());
		iMin = eval(getFournisseursRetenus());
		
		//choix des autres fournisseurs
		while (nbFournisseurs > 0) {
			
			for (int i = 0; i < getFournisseurs().size(); i++) {
				iFournisseurs.add(getFournisseurs().get(i));
				if (eval(iFournisseurs) < iMin) {
					iMin = eval(iFournisseurs);
					fournisseurTemp = getFournisseurs().get(i);
				}
				iFournisseurs.remove(getFournisseurs().get(i));
			}
			
			if(fournisseurTemp != null) {
				getFournisseursRetenus().add(fournisseurTemp);
				iFournisseurs = new ArrayList<>(getFournisseursRetenus());
			}
			
			fournisseurTemp = null;
			nbFournisseurs--;
		}
		
		return getFournisseursRetenus();
	}

	/**
	 * Trie les couts des clients par fournisseur par ordre croissant
	 * */
	public void trierCoutConnexion(List<Fournisseur> fournisseurs) {
		for (Fournisseur fournisseur : fournisseurs) {
			Collections.sort(fournisseur.getCoutsClients());
		}
	}
	
	/**
	 * Alpha : le client (pas connecté) qui a la cout minimum parmis les fournisseurs ouverts
	 * */
	public int alpha() {
		int min = Integer.MAX_VALUE;

		for (int idClient : getLesClients().keySet()) {
			if(!getLesClients().get(idClient)) { //client non connecté
				for (Fournisseur fournisseur: getFournisseursRetenus()) {
					if(fournisseur.getCoutsClients().get(idClient) < min) {
						min = fournisseur.getCoutsClients().get(idClient); 
					}
				}
			}
		}
		
		return min;
	}
	
	/**
	 * le cout d'un client parmis les fournisseurs ouverts 
	 * */
	public int meilleurCoutClientFournisseursOuverts(int idClient) {
		int minClientJFournisseursOuverts = Integer.MAX_VALUE;
		
		if (getFournisseursRetenus().isEmpty()) {
			minClientJFournisseursOuverts=0;
		}
		
		//les fournisseurs ouverts
		for (Fournisseur fournisseur: getFournisseursRetenus()) {
			if(fournisseur.getCoutsClients().get(idClient) < minClientJFournisseursOuverts) { //un fournisseur avec cout meilleur pour ce client
				minClientJFournisseursOuverts = fournisseur.getCoutsClients().get(idClient); 
			}
		}
		return minClientJFournisseursOuverts;
	}
	
	
	/**
	 * Calcul de 2 * fi - SOMMES...............
	 * */
	public int deuxfi_SommeDiffPositives(Fournisseur fournisseur_i) {
		int somme = 0;
		int sommetmp = 0;
		
		for (int idClient : getLesClients().keySet()) {//tous les clients
			if(getLesClients().get(idClient)) { //client connecté
				sommetmp = meilleurCoutClientFournisseursOuverts(idClient) - fournisseur_i.getCoutsClients().get(idClient);
				if(sommetmp > 0) { //si c'est positif
					somme += sommetmp;
				}
			}
		}
		return 2*(fournisseur_i.getCoutOuverture())-somme;
	}
	
	/**
	 * Beta :
	 * */
	
	public float beta(Fournisseur fournisseur_i) {		
		float min = Float.MAX_VALUE;
		float sommeYDansS = 0;
		float beta = deuxfi_SommeDiffPositives(fournisseur_i);
		
		clientsY = new HashMap<>();
		
		for (int idClientY : getLesClients().keySet()) {//tous les clients
			if(!getLesClients().get(idClientY)) {//client pas connecté
				getclientsY().put(idClientY, true); //augmente Y
				sommeYDansS += fournisseur_i.getCoutsClients().get(idClientY); //ajout d'un client à Y
				beta += sommeYDansS ; 
				beta /= getclientsY().size(); //divise par |Y|
				if (beta < min) {
					min = beta;
				}else {
					getclientsY().remove(idClientY); //
					beta=min;
					//System.out.println(getclientsY().size());
				}
			}
		}	
		
		return beta; 
	}
	
	
	public List<Fournisseur> glouton2() {
		float minAlpha = Float.MAX_VALUE; 
		float minBeta = Float.MAX_VALUE; 
		float minBetaTmp = Float.MAX_VALUE;
		int idClientAlpha = -1;
		int idFournisseurtBeta = -1;
		int nbClients = getFournisseurs().get(0).getCoutsClients().size();

		while (nbClients > 0) {
			
			minBeta = Float.MAX_VALUE;
			minBetaTmp = Float.MAX_VALUE;
			
			//alpha min
			minAlpha = alpha();
			
			//beta min
			for (int fournisseur_i : getFournisseursOuvertsFermes().keySet()) {
				if(!getFournisseursOuvertsFermes().get(fournisseur_i)) {
					minBetaTmp = beta(getFournisseurs().get(fournisseur_i));
					if(minBetaTmp < minBeta) {
						minBeta = minBetaTmp;
						idFournisseurtBeta = fournisseur_i;
					}
				}
			}
			
			if(minAlpha <= minBeta) {
				int min = Integer.MAX_VALUE;
				for (int idClient : getLesClients().keySet()) {
					if(!getLesClients().get(idClient)) {
						for (Fournisseur fournisseur: getFournisseursRetenus()) {
							if(fournisseur.getCoutsClients().get(idClient) < min) {
								min = fournisseur.getCoutsClients().get(idClient); 
								idClientAlpha = idClient;
							}
						}
					}
				}
				//System.out.println("alpha"+minAlpha);
				getLesClients().put(idClientAlpha, true);
				nbClients--;
			}else {
				beta(getFournisseurs().get(idFournisseurtBeta));
				System.out.println(getclientsY().size());
				for (int idClientY : getclientsY().keySet()) {
					getLesClients().put(idClientY, true);
					nbClients--;
				}
				//System.out.println("beta"+minBeta);
				getFournisseursOuvertsFermes().put(idFournisseurtBeta, true);
				getFournisseursRetenus().add(getFournisseurs().get(idFournisseurtBeta));
			}
			
		}
		
		
		return getFournisseursRetenus();
	}
	
	public List<Fournisseur> getFournisseurs() {
		return fournisseurs;
	}

	public void setFournisseurs(List<Fournisseur> fournisseurs) {
		this.fournisseurs = fournisseurs;
	}

	public List<Fournisseur> getFournisseursRetenus() {
		return fournisseursRetenus;
	}

	public void setFournisseursRetenus(List<Fournisseur> fournisseursRetenus) {
		this.fournisseursRetenus = fournisseursRetenus;
	}

	public Map<Integer, Boolean> getLesClients() {
		return lesClients;
	}

	public void setLesClients(Map<Integer, Boolean> lesClients) {
		this.lesClients = lesClients;
	}

	public Map<Integer, Boolean> getFournisseursOuvertsFermes() {
		return fournisseursOuvertsFermes;
	}

	public void setFournisseursOuvertsFermes(Map<Integer, Boolean> fournisseursOuvertsFermes) {
		this.fournisseursOuvertsFermes = fournisseursOuvertsFermes;
	}

	public Map<Integer, Boolean> getclientsY() {
		return clientsY;
	}

	public void setclientsY(Map<Integer, Boolean> clientsY) {
		this.clientsY = clientsY;
	}
	
	
	
}
