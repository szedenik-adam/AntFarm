package test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;

import skeleton.FieldCoordinate;
import map.Map;

public class TestRun {
	
	private static boolean started = false;
	private static boolean testSuccessfull = true;

	//A map objektum amelyben a program fog futni:
	private static Map map = null;
	private static Snapshot currentSnapshot = null;
	
	//jatekbeallitasok, futasi idoben nem modosithatok:
	private static int killerSprayRadius = 2;
	private static int neutralizerSprayRadius = 3;
	private static int killerSprayCharges = 30;
	private static int neutralizerSprayCharges = 50;
	
	private static FileWriter log;
	//private static int round = 0;
	
	private static String[] fileList = new String[] {
		/**/"test/in1.txt",
		"test/in2.txt",
		"test/in3.txt",
		"test/in4.txt",
		"test/in5.txt",
		"test/in6.txt",
		"test/in7.txt",
		"test/in8.txt",
		"test/in9.txt",
		"test/in10.txt",
		"test/in11.txt",/**/
	};
	static String newLine = System.getProperty("line.separator");
	
	public static void main(String[] args) {
		for (String temp : fileList) {
			try {
				run(new FileReader(new File(temp)));
				System.out.println(temp);
			} catch (FileNotFoundException e) {
				//e.printStackTrace();
			}
		}
	}
	
	//A kod futtatasat es interpretalasat vegzo fuggveny:
	public static void run(Reader code) {
		try {
			BufferedReader reader = new BufferedReader(code);
			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				String[] cmd = line.split(" ");
				try {
					if (cmd[0].equals("start")) {
						if (cmd.length!=1 && cmd.length!=2) throw new Exception("Invalid argument list!");
						if (cmd.length > 1)
							start(cmd[1]);
						else start(null);
					}
					else if (cmd[0].equals("load")) {
						if (cmd.length!=2) throw new Exception("Invalid argument list!");
						load(cmd[1]);
					}
					else if (cmd[0].equals("save")) {
						if (cmd.length!=2) throw new Exception("Invalid argument list!");
						save(cmd[1]);
					}
					else if (cmd[0].equals("do")) {
						if (cmd.length!=2) throw new Exception("Invalid argument list!");
						do_(Integer.valueOf(cmd[1]));
					}
					else if (cmd[0].equals("shoot")) {
						if (cmd.length!=4) throw new Exception("Invalid argument list!");
						shoot(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]), cmd[3]);
					}
					else if (cmd[0].equals("random")) {
						if (cmd.length!=2) throw new Exception("Invalid argument list!");
						random(cmd[1].equals("true"));
					}
					else if (cmd[0].equals("equals")) {
						if (cmd.length<1 || cmd.length>6) throw new Exception("Invalid argument list!");
						String[] cArgs = new String[cmd.length - 2];
						for (int i=2; i<cmd.length; i++) cArgs[i-2]=cmd[i];
						equals(cmd[1], cArgs);
					}
					else if (cmd[0].equals("not_equals")) {
						if (cmd.length<1 || cmd.length>6) throw new Exception("Invalid argument list!");
						String[] cArgs = new String[cmd.length - 2];
						for (int i=2; i<cmd.length; i++) cArgs[i-2]=cmd[i];
						notEquals(cmd[1], cArgs);
					}
					else if (cmd[0].equals("print")) {
						if (cmd.length!=1) throw new Exception("Invalid argument list!");
						print();
					}
					else if (cmd[0].equals("end")) {
						if (cmd.length!=1) throw new Exception("Invalid argument list!");
						end();
					}
					else throw new Exception("Invalid command name!");
				}
				catch (Exception e) {
					out(e.toString());
					//e.printStackTrace();
				}
			}
			reader.close();
		}
		catch (Exception e) {
			System.out.println(e.toString());
			//e.printStackTrace();
		}
	}
	
	/*Input parancsok megvalositasasat vegzo fuggvenyek:*/
	
	private static void start(String path) throws Exception {
		if (started) 
			end();
		started = true;
		testSuccessfull = true;
		if (path!=null) {
			File f = new File(path);
			if (f.exists()) f.delete();
			f.createNewFile();
			log = new FileWriter(f);
		}
		out("Test started ("+path+")!");
	}
	private static void load(String path) throws Exception {
		Snapshot ss = new Snapshot();
		ss.load(new File(path));
		map = ss.createMap(Map.random);
		out("Snapshot loaded ("+path+")!");
	}
	private static void save(String path)  {
		//throw new Exception("Save command cannot be used!");
	}
	private static void do_(int rounds) throws Exception {
		for (int i=0; i<rounds; i++)
			map.nextRound();
		//round += rounds;
		out(rounds + " rounds done!");
	}
	private static void shoot(int posX, int posY, String type) throws Exception {
		if (type.equals("AntKiller"))
			map.getAntKillerSpray().shoot(map.getFieldByCoordinate(new FieldCoordinate(posX, posY)), map.getRound());
		else if (type.equals("OdorNeutralizer"))
			map.getOdorNeutralizerSpray().shoot(map.getFieldByCoordinate(new FieldCoordinate(posX, posY)), map.getRound());
		else throw new Exception("Invalid argument: spray type");
		out(posX + ", " + posY + " shot with " + type);
	}
	private static void random(boolean val) throws Exception {
		Map.random = val;
	}
	private static void equals(String path, String[] optionalArgs) throws Exception {
		byte param = 0;
		for (int i=0; i<optionalArgs.length; i++) {
			if (optionalArgs[i].equals("-o")) param |= 1;
			else if (optionalArgs[i].equals("-a")) param |= 2;
			else if (optionalArgs[i].equals("-f")) param |= 4;
			else if (optionalArgs[i].equals("-e")) param |= 8;
			else throw new Exception("Invalid option");
		}
		Snapshot target = new Snapshot();
		target.load(new File(path));
		Snapshot current = new Snapshot(map);
		if (current.equals(target, param))
			out("Comparing succeed: current matches the target");
		else {
			testSuccessfull = false;
			out("Comparing failed: current differs from the target");
		}
	}
	private static void notEquals(String path, String[] optionalArgs) throws Exception {
		int param = 0;
		for (int i=0; i<optionalArgs.length; i++) {
			if (optionalArgs[i].equals("-o")) param |= 1;
			else if (optionalArgs[i].equals("-a")) param |= 2;
			else if (optionalArgs[i].equals("-f")) param |= 4;
			else if (optionalArgs[i].equals("-e")) param |= 8;
			else throw new Exception("Invalid option!");
		}
		Snapshot target = new Snapshot();
		target.load(new File(path));
		Snapshot current = new Snapshot(map);
		if (current.equals(target, param)) {
			testSuccessfull = false;
			out("Comparing failed: current matches the target");
		}
		else
			out("Comparing succeed: current differs from the target");
	}
	private static void print() throws Exception {
		out("");
		out("Printing the current state of the map at " + map.getRound() + " round(s)");
		out(map.PrintMapSmall());
		out(new Snapshot(map).print());
	}
	private static void end() throws Exception {
		if (started) {
			started = false;
			map = null;
			if (testSuccessfull)
				out("Test ended! Test result: succeed");
			else
				out("Test ended! Test result: failed");
			if (log!=null) log.close();
		}
		else throw new Exception("No test started!");
	}
	
	//Egyeb segedfuggvenyek:
	
	private static void out(String str) throws Exception {
		System.out.println(str);
		if (log!=null) {
			log.write(str + newLine);
		}
	}

}
