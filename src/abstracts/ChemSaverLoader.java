package abstracts;

import java.util.Scanner;

import universe.Element;

/*
 * This class saves the state of the Chemistry abstract to file
 * and recreates the state from a file and alters Chemsistry accordingly
 */
public class ChemSaverLoader implements Savable{
	
	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		String string = "";
		for(int i = 0; i < Chemistry.elements.size(); i++){
			if(i > 0){
				string += delims[dIndex];
			}
			Element el = Chemistry.elements.get(i);
			long eKey = saver.freeKey();
			saver.mapObject(eKey, el);
			string += el.save(saver, delims, dIndex+1) + delims[dIndex] + eKey;
		}
		return string;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		Chemistry.hardReset();
		while(scan.hasNext()){
			Element el = (Element) new Element(null, null, null, null, null).load(saver, delims, dIndex+1, scan.next());
			long eKey = scan.nextLong();
			saver.mapObject(eKey, el);
			Chemistry.elements.add(el);
			Chemistry.registerSymbol(el.getSymbol());
			Chemistry.removeAvailableName(el.getName());
		}
		scan.close();
		return null;
	}

}
