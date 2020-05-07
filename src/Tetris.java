import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.lcdui.Graphics;

public class Tetris extends MIDlet implements CommandListener{
static Thread moveThread;
static List menuXX, menuSaves;
static Command ok, men, s, qOk;
static Command play, del, playEnd, back, back2;
static Form quest;

static Display display;
Gl g;
TaktSyn TS1;
TextBox TB;
RecPlay RP;


public void pauseApp() {
}

public void destroyApp(boolean dede){
dede=false;
}

public void startApp(){

g = new Gl(); 
men = new Command("����", Command.OK, 1);

g.addCommand(men);
g.setCommandListener(this);	


TS1 = new TaktSyn(g, this);
//System.out.println("����� �������");
TS1.start();

menuXX = new List("��-����", List.IMPLICIT, new String[] {"����� ����", "����������", "���������", "�����"}, null);

ok = new Command("Ok", Command.OK, 1);
menuXX.addCommand(ok);
menuXX.setCommandListener(this);

display = Display.getDisplay(this);
display.setCurrent(menuXX);

}

public void commandAction(Command c, Displayable d) {

if (d==menuXX){
    String curM;
	int num;
	num=menuXX.getSelectedIndex();
	curM=menuXX.getString(num);
	//System.out.println("������ ����� ���� - "+curM);
    
	if (curM=="����� ����"){ //����� ����
					
			Figure F;
			TaktSyn.game();			
			display.setCurrent(g);
			TaktSyn.pauseKey = false;

		}

	if (curM=="�����"){
	   destroyApp(true);
	}
    
	if (curM=="����������"){
	   TaktSyn.pauseKey = false;
	   display.setCurrent(g);
	}
	
	if (curM=="���������"){
		
			s = new Command("���������", Command.CANCEL, 1);
			TB = new TextBox("Name ", "game"+Figure.ranDom(9999), 10, TextField.ANY);
			TB.addCommand(s);
			TB.setCommandListener(this);
		
		
	
		display.setCurrent(TB);		
	}
	
	if (curM=="����������") {
		back = new Command("�����",Command.BACK,1);
	    play =   new Command("�������������",Command.OK,1);
		del =    new Command("�������",Command.OK,3);
		playEnd =new Command("����������",Command.OK,4);
		back2 = new Command("������",Command.OK,5);
		
		String[] names;
		names=RecPlay.getNames("temp");
		if (names!=null){ menuSaves = new List("������������", List.IMPLICIT, names, null);
		                menuSaves.addCommand(play);						
						menuSaves.addCommand(del);
						menuSaves.addCommand(playEnd);
						menuSaves.addCommand(back);
						menuSaves.addCommand(back2);
						menuSaves.setCommandListener(this);
						display.setCurrent(menuSaves);
						//System.out.println("���!");
						} else {
						
						quest = new Form("������");
						quest.append("���-��� ��� ����������...");
						quest.setCommandListener(this);
						qOk = new Command ("OK", Command.OK,1);
						quest.addCommand(qOk);

						display.setCurrent(quest);
						}
	
	}

}
  
if (d==g) {
			TaktSyn.pauseKey = true;
			menuXX = new List("��-����", List.IMPLICIT, new String[] {"����� ����", "����������", "����������", "�����"}, null);
			ok = new Command("Ok", Command.OK, 1);
			menuXX.addCommand(ok);
			menuXX.setCommandListener(this);

			
			if (RecPlay.stateKey == RecPlay.RECORD){
				menuXX.insert(2,"���������", null);
				menuXX.insert(3,"����", null);
				
			}
			
			display.setCurrent(menuXX);
			}

			

if (d==TB){
	//System.out.println("� ���� ����� - "+TB.getString());
	String[] names;
	names=RecPlay.getNames("temp");
	for (int i=0; i<names.length; i++) {
	if (names[i].equals(TB.getString())) { 
							quest = new Form("!!!");
							quest.append("����, ����-�� ������, ����, �����...");
							quest.setCommandListener(this);
							qOk = new Command ("OK", Command.OK,1);
							quest.addCommand(qOk);

							display.setCurrent(quest);
							return;
							}
				
				
				
	}

	RecPlay.save(TB.getString());
    display.setCurrent(menuXX);
}
			
if (d==menuSaves) {
	if (c==play) {
	    
		
		
						//System.out.println("������ ���������������");
				

				RecPlay.RP(RecPlay.PLAY, menuSaves.getString(menuSaves.getSelectedIndex()));
				TaktSyn.pauseKey=false;
				display.setCurrent(g);

				

		
		}
	
		

	
	
    
	if (c==playEnd){


				RecPlay.RP(RecPlay.CONTINUE, menuSaves.getString(menuSaves.getSelectedIndex()));
				TaktSyn.pauseKey=false;
				display.setCurrent(g);

				
				
	}


	if (c==del) {
		RecPlay.delRec(menuSaves.getString(menuSaves.getSelectedIndex()));
		menuXX.setSelectedIndex(1,true);
		commandAction(ok, menuXX);
	}
	if (c==back){
	display.setCurrent(menuXX);
	
	}
    
	if (c==back2){
	commandAction(ok, menuXX);
	}
	
}
			
if (d==quest){

			display.setCurrent(menuXX);	


			}
}


public void gameEnd(){

			menuXX = new List("��-����", List.IMPLICIT, new String[] {"����� ����", "����������", "�����"}, null);
			ok = new Command("Ok", Command.OK, 1);
			menuXX.addCommand(ok);
			menuXX.setCommandListener(this);
			display.setCurrent(menuXX);
		
	
	
}


}
