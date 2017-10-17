package abstracts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.GameData;
import player.Astrometer;
import player.Beacon;
import player.ElementStack;
import player.Engine;
import player.Item;
import player.LifeSupport;
import player.Research;
import player.Spectrometer;
import surface.Cave;
import surface.Depot;
import surface.Feature;
import surface.Field;
import surface.Shipyard;
import surface.Vault;

public class SaverLoader implements Saver{
//	private static final char S = File.separatorChar;
	public static final String EXTENSION = ".space";
	public static final char[] delimiters =
		{'!', '@', '#', '&', '%', '~', '`', '_', '=', ';', '+', '^', '/', '\\',
		'"', 'À', '±', '²', '³', '´', 'µ', '¶', '¹', '…', 'ƒ', '„', '†', '‡', '‰',
		'Š', 'Œ', 'Ž', '•', '™', 'œ', 'Ÿ', '¢', 'À', '¥', '¦', '§', '¨', '©', 'ª',
		'«', '¬', '®', '¯', '°', 'º', '»'};
	private static Map<Long, Object> map;
	private PrintWriter writer;
	private List<Beacon> beaconsToActivate;
	private String lastUsedLoadFileName;
	
	public SaverLoader(){
		map = new HashMap<>();
		setLastUsedLoadFileName("spaceTradeSave");
	}

	public int save(GameData gd, String fileName){
		map.clear();
		File file = new File(getSavePath() + File.separatorChar + fileName + EXTENSION);
		try {
			writer = new PrintWriter(file, "UTF-8");
			String everything = new ChemSaverLoader().save(this, delimiters, 1);
			everything += delimiters[0];
			everything += Tipper.save(this, delimiters, 1);
			everything += delimiters[0];
			everything += gd.save(this, delimiters, 1);
			writer.print(everything);
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			return 1;
		}
		return 0;
	}

	public String getSavePath() {
		return new File("").getAbsolutePath();
	}
	
	@SuppressWarnings("finally")
	public GameData load(String fileName){
		map.clear();
		String everything = "";
	    try {
			BufferedReader br = new BufferedReader(new FileReader(getSavePath() + File.separatorChar + fileName + EXTENSION));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        everything = sb.toString();
	        br.close();
	    } finally {
	    	try{
	    		beaconsToActivate = new ArrayList<>();
		    	Scanner scan = new Scanner(everything);
		    	scan.useDelimiter(delimiters[0]+"");
		    	new ChemSaverLoader().load(this, delimiters, 1, scan.next());
		    	Tipper.load(this, delimiters, 1, scan.next());
		    	GameData gd = (GameData) new GameData(null).load(this, delimiters, 1, scan.next());
		    	scan.close();
			    
		    	gd.getPlayer().clearAllBeacons();
		    	for(Beacon b : beaconsToActivate){
		    		gd.getPlayer().addActiveBeacon(b);
		    	}
		    	return gd;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		return null;
	    	}
	    }
	}

	

	@Override
	public Item loadItem(String data, int dIndex) {
		if(data.equals(" ")){
			return null;
		}
		String swi = "";
		try{
			swi = data.substring(0,5);
		}catch(Exception e){
			System.out.println("ERR WITH: <" + data + ">");
			throw new Error();
		}
		switch(swi){
		case "eleme":{return (Item) new ElementStack(null, 0).load(this, delimiters, dIndex, data);}
		case "astro":{return (Item) new Astrometer(0).load(this, delimiters, dIndex, data);}
		case "beaco":{return (Item) new Beacon(null, 0).load(this, delimiters, dIndex, data);}
		case "engin":{return (Item) new Engine(0,0,0,null).load(this, delimiters, dIndex, data);}
		case "lifes":{return (Item) new LifeSupport(null, 0).load(this, delimiters, dIndex, data);}
		case "spect":{return (Item) new Spectrometer(0).load(this, delimiters, dIndex, data);}
		case "resea":{return (Item) new Research(null, 0).load(this, delimiters, dIndex, data);}
		}
		System.out.println("couldn't match: <" + swi + ">");
		throw new Error();
	}

	@Override
	public Feature loadFeature(String data, int dIndex) {
		String swi = data.substring(0,5);
		switch(swi){
		case "cave ":{return (Feature) new Cave(0,0,null).load(this, delimiters, dIndex, data);}
		case "depot":{return (Feature) new Depot(0,0,null).load(this, delimiters, dIndex, data);}
		case "field":{return (Feature) new Field(0,0,null,null).load(this, delimiters, dIndex, data);}
		case "vault":{return (Feature) new Vault(0,0,null).load(this, delimiters, dIndex, data);}
		case "shipy":{return (Feature) new Shipyard(0,0,null).load(this, delimiters, dIndex, data);}
		}
		System.out.println("couldn't match: <" + swi + ">");
		throw new Error();
	}

	@Override
	public void activateBeacon(Beacon b) {
		beaconsToActivate.add(b);
	}

	public String getLastUsedLoadFileName() {
		return lastUsedLoadFileName;
	}

	public void setLastUsedLoadFileName(String lastUsedLoadFileName) {
		this.lastUsedLoadFileName = lastUsedLoadFileName;
	}

	@Override
	public void mapObject(long key, Object value) {
		map.put(key, value);
	}

	@Override
	public Object getObject(long key) {
		if(!map.containsKey(key)){
			throw new Error();
		}
		return map.get(key);
	}

	@Override
	public long freeKey() {
		long l = 0;
		do{
			l = (long) (Math.random()*Long.MAX_VALUE);
		}while(map.containsKey(l));
		return l;
	}

	@Override
	public long getKeyFor(Object o) {
		for(long k : map.keySet()){
			if(map.get(k) == o){
				return k;
			}
		}
		throw new Error();
	}
}
