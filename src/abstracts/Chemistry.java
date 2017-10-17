package abstracts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import main.Ticker;
import universe.Element;
import universe.Point;

/*
 * Class populates and adds to the game's periodic table.
 * Also distributes Elements to requesting game data
 */
public abstract class Chemistry {
	private static final int MAX_UNIQUE_ELEMENTS = 60;
	private static int minColorDistance = 200;
	public static List<Element> elements;
	private static List<String> elementNames;
	private static List<String> elementSymbols;
	private static String[] names = {"Almantium", "Aluric", "Andogal", "Atkinsonium", "Mephisto", "Bensite",
		"Caldium", "Cerium", "Cordinium", "Dexium", "Dolerus", "Dollium", "Durall", "Eddyrium", "Endium",
		"Ephisteum", "Fallium", "Fandurim", "Faurdum", "Firdunum", "Foin", "Folum", "Galsim", "Gerrium", "Harum",
		"Hedium", "Hocodurum", "Illirium", "Imudant", "Incindric", "Inderum", "Jalix", "Jarlam", "Jermanix",
		"Jurum", "Kaldan", "Kallicite", "Kophite", "Landum", "Larsix", "Leukonix", "Maulicum", "Maurite",
		"Melrium", "Mondosite", "Morum", "Mothicite", "Nadite", "Neotite", "Obstite", "Ogrum", "Okite",
		"Ollurium", "Olrium", "Orrulite", "Orsite", "Pallam", "Pholite", "Pidisum", "Pontite", "Qaldinium",
		"Qorimite", "Quasite", "Rabnalium", "Rasix", "Rellium", "Rhenon", "Rhobum", "Rhonkite", "Sallix",
		"Sancizite", "Sandorite", "Sednium", "Selrium", "Sisite", "Sorix", "Tandium", "Theron", "Thubite",
		"Tobolum", "Truncite", "Ubnerium", "Ullium", "Uvumite", "Vacertite", "Valberium", "Vallite", "Vebium",
		"Vopuzite", "Walcium", "Wandun", "Yaldium", "Yokum", "Zarrite", "Zehtium", "Zorbalum", "Adrium", "Bolon",
		"Ronurite", "Fezium", "Zurbon", "Obalum", "Horax", "Kroysten", "Bangusten", "Denaduin", "Adram", "Adantite",
		"Bhoshrite", "Yalbacin", "Uboric", "Thildorin", "Hadskil", "Nodenine", "Phistite", "Ehicrine", "Ollers",
		"Kaldresite", "Morin", "Whoris", "Kugrum", "Eswalt", "Batril", "Yathine", "Traytine", "Flodian", "Iufrite",
		"Blosten", "Eblum", "Claecium", "Abril", "Uflyx", "Qascalt", "Nachyx", "Woskyx", "Vutril", "Yakrum", "Ablian",
		"Tuslian", "Teclite", "Ecrese", "Canadium", "Uswil", "Acralt", "Mobrum", "Qeswyx", "Daplian", "Toglese",
		"Sloitine", "Focrite", "Iascil", "Xeskum", "Putrum", "Sciothil", "Kestine", "Thetine", "Cluontine", "Hogrium",
		"Mabrum", "Fecril", "Dedryx", "Duchalt", "Brulium", "Xuslium", "Bluotine", "Puthese", "Ospite", "Totrium",
		"Triuthil", "Buslalt", "Sabril", "Feslum", "Aplian", "Fachian", "Stoenium", "Yasnil", "Spoecium", "Vobrite",
		"Meprese"};
	
	//resets the periodic table, starts anew
	public static void hardReset(){
		elements = new ArrayList<>();
		elementNames = new ArrayList<>();
		elementSymbols = new ArrayList<>();
		Random r = new Random();
		for(int i = 0; i < names.length; i++){
			elementNames.add(names[i]);
		}
		Collections.shuffle(elementNames, r);
	}
	
	public static void init() {
		hardReset();
	}
	
	public static String getElementName(int index){
		expandNamesIfNecessary(index);
		return elements.get(index).getName();
	}

	private static void expandNamesIfNecessary(int index) {
		while(elements.size() < index + 1){
			createElement();
			Ticker.spin();
		}
	}

	/*
	 * creates a new element. If given a point, will favour creating a locus there for the
	 * new element.
	 */
	private static Element createElement(Point... p) {
		String name = elementNames.remove(0);
		List<Restriction> prohibited = Restriction.randomSetWithProb(.16, true);
		prohibited.remove(Restriction.IS_STAR);
		List<Restriction> required = Restriction.randomSetWithProb(.09, false);
		required.removeAll(prohibited);
		if(elements.size() > MAX_UNIQUE_ELEMENTS*2/3){
			required.clear();
		}
		if(elements.size() > MAX_UNIQUE_ELEMENTS*4/5){
			prohibited.clear();
		}
		Element e = new Element(name, registerSymbolOf(name), nextAvailableColor(), prohibited, required, p);
		elements.add(e);
		return e;
	}

	/*
	 * Generates and remembers a unique string symbol for a given element name.
	 */
	private static String registerSymbolOf(String name) {
		String symbol = "";
		for(int i = 0; i < name.length(); i++){
			symbol += name.charAt(i);
			if(i == 0){
				symbol = symbol.toUpperCase();
			}
			if(!elementSymbols.contains(symbol)){
				elementSymbols.add(symbol);
				return symbol;
			}
		}
		return name;
	}

	/*
	 * Returns new next color found that is sufficiently "far
	 * away from other registered colors
	 */
	private static Color nextAvailableColor() {
		Color col;
		int i = 0;
		do{
			col = new Color(Fun.rng(256), Fun.rng(256), Fun.rng(256));
			Ticker.spin();
			i++;
			if(i > 50){
				minColorDistance = (int) (minColorDistance*.9) + 1;
				i = 0;
			}
		}while(!colorIsAvailable(col));
		return col;
	}
	
	/*
	 * returns if a given color is sufficiently far enough from all registered colors
	 */
	private static boolean colorIsAvailable(Color col){
		if(colorDistance(col, Color.BLACK) < minColorDistance ||
				colorDistance(col, Color.WHITE) < minColorDistance ||
				colorDistance(col, Color.GRAY) < minColorDistance ||
				colorDistance(col, Color.BLUE) < minColorDistance ||
				colorDistance(col, Color.RED) < minColorDistance ||
				colorDistance(col, Color.DARK_GRAY) < minColorDistance){
			return false;
		}
		for(Element e : elements){
			if(colorDistance(e.getCol(), col) < minColorDistance){
				return false;
			}
		}
		return true;
	}

	private static int colorDistance(Color a, Color b) {
		return Math.abs(a.getRed() - b.getRed()) +
				Math.abs(a.getBlue() - b.getBlue()) +
				Math.abs(a.getGreen() - b.getGreen());
	}

	/*
	 * Retrieves an appropriate element from the periodic table, for the caller
	 * given the restrictions of the caller. Generates new elements as no matching
	 * elements are found.
	 */
	public static Element getElement(Point c, List<Restriction> restrictions){
		if(c.outsideUniverse()){
			throw new Error();
		}
		double graceDistance = 0;
		int i = 0;
		while(true){
			if(i >= elements.size()){
				if(elementNames.size() > 0 && elements.size() < MAX_UNIQUE_ELEMENTS){
					return createElement(c.clone());
				}else{
					for(Element el : elements){
						el.addRandomLoci(1);
					}
					graceDistance += 0.03;
					i = 0;
				}
			}
			Element e = elements.get(i);
			if(e.suitabilityCode(c, restrictions, graceDistance) == 0){
				bumpElementForward(e);
				return e;
			}
			i++;
			Ticker.spin();
		}
	}

	/*
	 * Re-shuffles elements to optimize the time it takes for consecutive local getElement() calls.
	 */
	private static void bumpElementForward(Element e) {
		if(elements.size() < 4){
			return;
		}
		elements.remove(e);
		elements.add(3, e);
		Element last = elements.get(elements.size()-1);
		elements.remove(last);
		elements.add(0, last);
	}

	public static List<Element> getElements() {
		return elements;
	}

	/*
	 * Returns an estimate for the distance a body at point p
	 * would likely have to travel to find element e
	 */
	public static double distanceToElementFrom(Element e, Point p, List<Restriction> restrictions) {
		double dist = 0.00001;
		Point p2 = p;
		do{
			p2 = p.dirOffset(Fun.rdg(Math.PI*2), dist);
			dist *= 1.8 + 0.000001;
			if(dist > 1.4){
				return 1.4;
			}
			Ticker.spin();
		}
		while(p2.outsideUniverse() || getElement(p2, restrictions) != e);
		return dist;
	}

	public static void registerSymbol(String symbol) {
		elementSymbols.add(symbol);
	}

	public static void removeAvailableName(String name) {
		elementNames.remove(name);
	}
	
}
