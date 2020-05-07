import java.util.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStoreException;

public class RecPlay {

private static byte[] eventRec; //������ ��� ������ �������
private static int counter; // ������� ������� ������� eventRec
private static int RMScounter; // ������� ������� ���������
// stateKey - ���������� ��������� ������� �����: 0 - ����� �������� ������ �� �����, 1 - ����� ������, 2 - ����� ��������������
public static int stateKey;
public static RecordStore       curRS;
public static RecordEnumeration curRE;
public static String nameRS = "temp";
public static final int RECORD = 1;
public static final int PLAY = 2;
public static final int CONTINUE = 3;



static{
stateKey=0;
}

public static String[] getNames(String naxyi){
String[] names, namesC;
int ch, ch2;
 names = RecordStore.listRecordStores();

if (names == null) return null;
if (naxyi == null) return names; //���������� ������ ������, ����� ������� �������� �������

ch2=0;
if (names.length>1) namesC = new String[names.length-1]; else namesC = new String[names.length];

boolean m = false;
for (ch=0; ch<names.length; ch++){
	if (!naxyi.equals(names[ch])) {
		if (ch2<namesC.length) 
		{
			namesC[ch2]=names[ch];
			//System.out.println("namesC = " + names[ch]);
			ch2++;
        };
	} else m=true;

}

if ((m) && (names.length==1)) return null;
if (m) return namesC; else return names;
}


public static void curClose(){
    try{
		curRS.closeRecordStore();
    } catch (RecordStoreException rse){
		System.out.println(rse.getMessage());
    }
}

public static void RP(int key, String name){
byte[] tmp;

   counter =0;
   TaktSyn.delta=0; //������ ���������������� ��������� ��� �������� ������
   TaktSyn.clearEvent(); // �������� ��� ���������� ��������

 if (key==RECORD){// ������ ������
   nameRS="temp";
   delRec("temp");
   eventRec = new byte[128];
      
   try{
    curRS = RecordStore.openRecordStore("temp", true);
   } catch (RecordStoreException rse){
    System.out.println(rse.getMessage());
   }
   recordCurPosition();
   TaktSyn.timeCH=0;

   Figure.stadoSyn();
   stateKey=key;
 }
  if (key==PLAY) { // ��������������� ����������� � ��������� nameRS
  
   if (RecPlay.stateKey == RECORD) endRec(); // ���� ��� ����� ������, ����������� ������, ��������� temp
   if (name!=null) nameRS=name;

   RMScounter=1;
	try{
		curRS = RecordStore.openRecordStore(nameRS, true);
		eventRec = curRS.getRecord(RMScounter);
		RMScounter++;
	} catch (RecordStoreException rse){
		System.out.println(rse.getMessage());
		}
	//System.out.println("������ RP, ������� ��������� - "+nameRS);


	counter =1;
	playCurPosition();
		
	TaktSyn.timeCH=1000; // ���������� ����� �� ����� ��� ���������� ������ �������-�������
    stateKey=key;
  
  }
  if (key==CONTINUE) {//������������� � �����
    
	stateKey=key;
	if (name!=null) nameRS=name;
	try{
		curRS = RecordStore.openRecordStore(nameRS, true);
		eventRec = curRS.getRecord(curRS.getNumRecords());
	} catch (RecordStoreException rse){
		System.out.println(rse.getMessage());
	}
	counter=4; //���������� ���� ����
	playCurPosition();
	}  


}


/* ��������������� ���������� �������� ������������ � ������������� �����, ������������ � ������ � � ����� ������� ����������, �������������� ����-
������ �� 3-� ���� FF FF FF */

public static void playCurPosition(){
byte res=0;
int ch=0;
Figure F;

res=eventRec[counter];
for (int y=0; y<20; y++){
    for (int x=0; x<10; x++){
   
      if (((res>>ch++) & 0X1) == 1) Pole.set(y,x,true); else Pole.set(y,x,false);
   if (ch>7) {ch=0; res=eventRec[++counter];}
  }
}
Figure.stado.setSize(0);
ch=0;
byte cf = eventRec[counter];
//System.out.println("������ ��������� ������ selectCurFig �������� = "+ cf);
if (eventRec[++counter]==1) TaktSyn.selectFig=true; else TaktSyn.selectFig=false;
counter++;

Figure.curSpeed=eventRec[counter];
//System.out.println("�������� ��������� = " + eventRec[counter]);
counter++;

/*��������� �������� Gl.resultL*/
Gl.resultL=(((eventRec[counter]) & (0XFF)) | ((eventRec[counter+1] <<8) & (0XFF00)) | 
           ((eventRec[counter+2]<<16) & (0XFF0000)) | ((eventRec[counter+3]<<24) & (0XFF000000)));
     counter+=4;
//System.out.println("����������� ���������� ����� - Gl.result ��������� = "+ Gl.resultL);
     
/*��������� �������� Gl.resultL*/
while (true) {
 F = new Figure( (eventRec[counter]/10),  //set
				 ((eventRec[counter]-((eventRec[counter]/10)*10))*Gl.kf), //PositionX
				 (((eventRec[counter+3]) & (0XFF)) | ((eventRec[counter+4] <<8) & (0XFF00))),	//PositionY
				 eventRec[counter+1],					//SpeedTime
				 eventRec[counter+2],					//RotatePosition
				 true);
  counter+=5;
  Figure.stado.addElement(F);

  if ((eventRec[counter] == -1) && (eventRec[counter+1] == -1) && (eventRec[counter+2] == -1)) break;
}

TaktSyn.selectCurFig = ((Figure)Figure.stado.elementAt(cf));
counter+=3;
}




/* ������ ������������ � ������������� �����, ������������ � ������ � � ����� ������� ����������, �������������� ����-
������ �� 3-� ���� FF FF FF */

public static void recordCurPosition(){
byte one =1;
byte res =0;
int ch=0;
int k12 =0;

for (int y=0; y<20; y++){
    for (int x=0; x<10; x++){
      if (Pole.get(y,x)) {res = (byte)((one<<ch)|(res)); //System.out.println("res = "+ res +"ch ="+ch +"one<<ch = "+ (byte)(one<<ch));
       }
   ch++;
      if (ch>7) {ch=0; 
         //System.out.println("Ch>7, ch=1; res=" + res);
      eventRec[++counter]= res;
      res=0;
      }
  }
}

short rec2=0;

eventRec[++counter]=(byte)(Figure.stado.indexOf(TaktSyn.selectCurFig));


//System.out.println("������� ����� ������ selectCurFig ������� = " + Figure.stado.indexOf(TaktSyn.selectCurFig));

if (TaktSyn.selectFig) eventRec[++counter]=(byte)1; else eventRec[++counter]=(byte)0;
eventRec[++counter]=(byte)Figure.curSpeed;

//System.out.println("������� �������� �������� = " + Figure.curSpeed);



/*--------------- ������ ����������� ����� -------------------*/
eventRec[++counter]= (byte)Gl.resultL;
eventRec[++counter]= (byte)(Gl.resultL>>8);
eventRec[++counter]= (byte)(Gl.resultL>>16);
eventRec[++counter]= (byte)(Gl.resultL>>24);
/*--------------- ������   ������ -------------------*/
//System.out.println("����������� ���������� ����� - Gl.resultL �������� = " + Gl.resultL);

for (int v=0; v<Figure.stado.size(); v++){
/*  System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
  System.out.println("������� � = "+ ((Figure)Figure.stado.elementAt(v)).getPositionX());
  System.out.println("������� � = "+ ((Figure)Figure.stado.elementAt(v)).getPositionY());
  System.out.println("set = "+ ((Figure)Figure.stado.elementAt(v)).set);
  System.out.println("�������� = "+ ((Figure)Figure.stado.elementAt(v)).speedTime);
  System.out.println("�������� = "+ ((Figure)Figure.stado.elementAt(v)).rotatePos);*/
  
  eventRec[++counter]=(byte)((((Figure)Figure.stado.elementAt(v)).set*10) + (((Figure)Figure.stado.elementAt(v)).getPositionX()/Gl.kf));
  
  eventRec[++counter]=(byte)(((Figure)Figure.stado.elementAt(v)).speedTime); 
  
  eventRec[++counter]=(byte)(((Figure)Figure.stado.elementAt(v)).rotatePos);
  
  rec2=(short)((Figure)Figure.stado.elementAt(v)).getPositionY();
  eventRec[++counter]=(byte)rec2;
  eventRec[++counter]=(byte)(rec2>>8);
    }

eventRec[++counter]=(byte)0XFF;
eventRec[++counter]=(byte)0XFF;
eventRec[++counter]=(byte)0XFF;

counter++;


}





public static int byteToInt256(byte b){

if (b<0) {
   return 128+(b & 127);
         } else return b;

}


public static void RMS(){
byte[] rec;    
 rec=eventRec;
 eventRec = new byte[128];
 counter=0;
 try{
  curRS.addRecord(rec, 0, rec.length);
 } catch (RecordStoreException rse){
  System.out.println(rse.getMessage());
 }
}


public static void readEV(){
//System.out.println("*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--");
if ((counter+2)>(eventRec.length-1)) {
 // System.out.println("counter+2 �������� ������ ������� eventRec ������ ��������� ������ - " + RMScounter);
  try{
   eventRec = curRS.getRecord(RMScounter);
   RMScounter++;
  } catch (RecordStoreException rse){
   System.out.println(rse.getMessage());
  }
     counter=0;
 }


if ((eventRec[counter+1] == -1) && (eventRec[counter+2] == -1) && (eventRec[counter+3] == -1)){ 
//   System.out.println("����� ��������������� FF FF FF");
   stateKey = 0;
   TaktSyn.timeEND=255;
   curClose();
   return;
   }
 // System.out.println("����� �� ������ RecPlay = "+ byteToInt256(eventRec[counter]) +" counter =" + counter);
  TaktSyn.timeCH=0;
  TaktSyn.timeEND=byteToInt256(eventRec[counter]);
  counter++;
//  System.out.println("������� �� ������ RecPlay = "+ byteToInt256(eventRec[counter]) +" counter =" + counter);
  TaktSyn.addEvent(byteToInt256(eventRec[counter]));
  counter++;
}





public static void endRec(){

//if ((counter+3)>(eventRec.length-1)){System.out.println("���������� ���������, � �����, ��� ����... ����� �� �������") RMS();}
RMS();

eventRec[++counter]=(byte)0XFF;
eventRec[++counter]=(byte)0XFF;
eventRec[++counter]=(byte)0XFF;


recordCurPosition();
RMS();
curClose();

}

//����� ������� ��������� ������� ���������� � ��������� delName, ���������� true ���� �������� �������...

public static boolean delRec(String delName){
String[] names;
names=getNames(null);
if (names == null) return false;
   
   for (int i=0; i<names.length; i++){
  //  System.out.println("������� ������������  " +names[i] + " " +nameRS );
    if (delName.equals(names[i])) {
   //  System.out.println("������� ���������"+ names[i]);
     
      try{
		RecordStore.deleteRecordStore(delName);
	  } catch (RecordStoreException rse){System.out.println(rse.getMessage());}
      return true;
    }
   }
   
return false;
}







public static void save(String saveName){
endRec();
copyRec(nameRS, saveName);
}

public static void copyRec(String openRec, String saveRec){
RecordStore openRS, saveRS;

byte[] buff;
 try{
  openRS = RecordStore.openRecordStore(openRec, true);
  saveRS = RecordStore.openRecordStore(saveRec, true);
 //  System.out.println("������ ������� ����������, openRS.getNumRecords() = "+openRS.getNumRecords());



		for(int i=1; i<=openRS.getNumRecords(); i++){
			buff=openRS.getRecord(i);
			saveRS.addRecord(buff,0,buff.length);
		}
	
	
 openRS.closeRecordStore();
 saveRS.closeRecordStore();
} catch (RecordStoreException rse){System.out.println(rse.getMessage());}

}



public static void playIt(){

}


public static void addEvent(int eventCode){
int deltaTime;
long thisTime;
short rec2=0;
Figure F2;

if ((counter+2)>(eventRec.length-1)){
//System.out.println("��������� ������� ������� RMS !!!!!");
RMS();
}

//System.out.println("timeCh ������ � ������� = " + TaktSyn.timeCH +"counter = " + counter);
eventRec[counter]=(byte)TaktSyn.timeCH; //������� �������� ��������!


counter++;
//System.out.println("��� ������� = " + eventCode + "counter = "+counter);

TaktSyn.timeCH = 0;

if (eventCode == 10) {
  
	F2 = ((Figure)Figure.stado.elementAt(Figure.stado.size()-1));
	//System.out.println("set = "+ F2.set);
	//System.out.println("PositionX = "+ (F2.getPositionX()/Gl.kf));
	eventRec[counter]=(byte)((F2.set*10)+(F2.getPositionX()/Gl.kf)+100);  
	//System.out.println("��������� ������, ��� = " + byteToInt256(eventRec[counter]));
	counter++;
   
} else {
		eventRec[counter] = (byte)eventCode;
		counter++;
}
//System.out.println("***********************************************************************************");
//if (counter>(eventRec.length-1)) RMS();



}


}