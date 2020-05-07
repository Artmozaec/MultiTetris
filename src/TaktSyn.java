import java.util.*;

public class TaktSyn extends Thread implements Const{
public static int timeCH, timeEND;
//public static boolean TSFlag;
public static boolean selectFig; //Ключ определяющий сотояние выбранной фигуры (активное можно производить действия, и состояние премещение выбора)
public static Figure selectCurFig;

public static boolean gameOverFlag;
private static int[] eventArr;
public static int eventN, delta;
private Gl G;
public  RecPlay RP;
public Tetris T;
public static boolean pauseKey;

static{
eventArr = new int[] {0,0,0,0,0,0,0,0};
eventN=0;
selectFig=false;
pauseKey=false;
delta=0;
}

TaktSyn(Gl gg, Tetris t){
G=gg;
T=t;
}

public static void addEvent(int evCode){
eventArr[eventN]=evCode;
eventN++;
}

public static void clearEvent(){
//System.out.println("Функция clearEvent, массив event-пуст");
for (int i=0; i<eventArr.length-1; i++) eventArr[i] = 0;
}

public static int readEvent(){
int ret;
ret = eventArr[0];
for (int i=0; i<eventArr.length-1; i++) eventArr[i] = eventArr[i+1];
eventN--;
return ret;
}

public void run(){

gameOverFlag=false;
pauseKey = true;

if (RecPlay.stateKey != RecPlay.PLAY) timeEND = 255; //При режиме воспроизведения первая задежка читается из RP.readEV() 
			  
while (true){
	timeCH=0;
		for (; ((timeCH<timeEND) && ((eventN == 0) || (RecPlay.stateKey == RecPlay.PLAY))); timeCH++){




			for (int v=0; v<Figure.stado.size(); v++){
			((Figure)Figure.stado.elementAt(v)).move(A_DOWN, 1);
			}

		    while (pauseKey) {
				if (gameOverFlag){ gameOverFlag = false;  T.gameEnd();}
				
				try{
				    this.sleep(100);
//					System.out.println("Пауза");
				}catch(InterruptedException e) {}
			}

			
			try{
				this.sleep(50);
			}catch(InterruptedException e) {}
//			System.out.println("Значение TimeCh = "+ timeCH + "  timeEND =  "+timeEND);
			G.repaint();
			
		}
 
	
	if ((eventN==0) && (RecPlay.stateKey == RecPlay.RECORD)){  
					//System.out.println("Вышло время основного цикла Run, timeCH =" + timeCH +" добавлено событие, код 0");
																					RecPlay.addEvent(0);
	  																			}
	if (eventN>0) eventOperate(readEvent());
			
	if (RecPlay.stateKey == RecPlay.PLAY) RP.readEV();
}
	

}

public static void eventOperate (int EC){

boolean sw = false;
Figure F;

//System.out.println("функция eventOperate, произошло действие, код - "+EC);

 if (EC==10) {
		F=new Figure(0, 0, 0, Figure.curSpeed-delta,0, false);
		if (delta>3) delta=0; else delta++;
		Figure.stado.addElement(F);
		if (RecPlay.stateKey == RecPlay.RECORD) RecPlay.addEvent(10);
		return;
		}
 if (EC>99){
  //System.out.println("Создана фигура должна быть");
  EC=EC-100;
  F=new Figure(EC/10, (EC-((EC/10)*10))*Gl.kf, 0, Figure.curSpeed-delta,0, true);
  Figure.stado.addElement(F);
  if (delta>3) delta=0; else delta++;
  return;
 }
 if ((EC==35) && (RecPlay.stateKey!=RecPlay.RECORD)) {            RecPlay.RP(RecPlay.RECORD, null); //Запись
						 return;
						  }
						  
 if ((EC==42) && (RecPlay.stateKey!=0)){ //Воспр !=0 Имеется в виду, что-бы не воспроизвести пустую запись
						 RecPlay.RP(RecPlay.PLAY, null);
						 return;
						 }
						 
 if (RecPlay.stateKey == RecPlay.RECORD) RecPlay.addEvent(EC);
 
 if (selectFig) {
    

	switch (EC) {

      	case 52:{ // ЛЕВО
         
		selectCurFig.move(A_LEFT, Gl.kf);
		
		              };  
		   break;

		case 54:{ // ПРАВО
		selectCurFig.move(A_RIGHT, Gl.kf);		   
			      };
		   break;

		case 56:{ // ВНИЗ
		selectCurFig.move(A_DOWN, Gl.kf);
			      };
                   break;

 		case 53:{ // Поворот
		selectCurFig.rotate(true);
	     		      };
	           break;


		case 49:{ //вход из выделения вверх, влево
				selectCurFig=shiftSelect(1);
				selectFig=false;
                              }
		   break;
		case 50:{ //вход из выделения вверх
				selectCurFig=shiftSelect(2);
				selectFig=false;
                              }
		   break;
		case 51:{ //вход из выделения вверх, вправо
				selectCurFig=shiftSelect(3);
				selectFig=false;
                              }
		   break;
		case 55:{ //вход из выделения вниз, влево
				selectCurFig=shiftSelect(7);
				selectFig=false;
                              }
		   break;
		case 57:{ //вход из выделения вниз, вправо
				selectCurFig=shiftSelect(9);
				selectFig=false;
                              }
        }
 } else { 
	switch (EC) {
		case 49: selectCurFig=shiftSelect(1); break;

		case 51: selectCurFig=shiftSelect(3); break;

		case 55: selectCurFig=shiftSelect(7); break;

		case 57: selectCurFig=shiftSelect(9); break;

      	case 52: selectCurFig=shiftSelect(4); break;

		case 54: selectCurFig=shiftSelect(6); break;

		case 50: selectCurFig=shiftSelect(2); break;

		case 56: selectCurFig=shiftSelect(8); break;

 		case 53: selectFig=true; break;

		};
		

	}


	  
	
	

 

}

public static Figure shiftSelect(int key){
Figure volum, tmp; 
int deltaX, deltaY, deltaFig=300;

volum=selectCurFig;


	for (int v=0; v<Figure.stado.size(); v++){

          tmp = (Figure)Figure.stado.elementAt(v); // Ну просто нереально длинные выражения!, сокращаем...
          if (tmp!=selectCurFig){
          switch (key) {
		case 4:{ //Лево
		   if ( (tmp.getPositionX()+tmp.shirina)-Gl.kf <= selectCurFig.getPositionX()) {
			deltaX=selectCurFig.getPositionX() - (tmp.getPositionX()+tmp.shirina);

			if (tmp.getPositionY() <= selectCurFig.getPositionY()){ // То есть выше
				deltaY = (selectCurFig.getPositionY()+selectCurFig.visota) - (tmp.getPositionY()+tmp.visota);
				} else {
				deltaY = tmp.getPositionY()-selectCurFig.getPositionY();
			        }
			
		        if ((deltaX+deltaY) < deltaFig){

			volum=tmp; 
			deltaFig = deltaX+deltaY;
			}
			}
		       };
		       break;

		case 6:{ //Право
		   if ( tmp.getPositionX() >= (selectCurFig.getPositionX()+selectCurFig.shirina)-Gl.kf) {

			deltaX=tmp.getPositionX()-(selectCurFig.getPositionX()+selectCurFig.visota);

			if (tmp.getPositionY() <= selectCurFig.getPositionY()){ // То есть выше
				deltaY = (selectCurFig.getPositionY()+selectCurFig.visota) - (tmp.getPositionY()+tmp.visota);
				} else {
				deltaY = tmp.getPositionY()-selectCurFig.getPositionY();
			        }
			
		        if ((deltaX+deltaY) < deltaFig){


			volum=tmp; 
			deltaFig = deltaX+deltaY;
			}
			}
		       };
		       break;

		case 2:{//вверх
		   if ((tmp.getPositionY() + tmp.visota)-Gl.kf <= selectCurFig.getPositionY()) {

		       deltaY = selectCurFig.getPositionY()-(tmp.getPositionY()+tmp.visota);

			if (tmp.getPositionX() <= selectCurFig.getPositionX()){ // То есть левее
				deltaX = (selectCurFig.getPositionX()+selectCurFig.shirina) - (tmp.getPositionX()+tmp.shirina);
				} else {
				deltaX = tmp.getPositionX()-selectCurFig.getPositionX();
			        }
		        if ((deltaX+deltaY) < deltaFig){


			volum=tmp; 
			deltaFig = deltaX+deltaY;

			}
			}
		       };
		       break;


		case 8:{//Вниз
		   if (tmp.getPositionY() >= (selectCurFig.getPositionY() + selectCurFig.visota)-Gl.kf) {

			deltaY = tmp.getPositionY()-(selectCurFig.getPositionY()+selectCurFig.shirina);

			if (tmp.getPositionX() <= selectCurFig.getPositionX()){ // То есть левее
				deltaX = (selectCurFig.getPositionX()+selectCurFig.shirina) - (tmp.getPositionX()+tmp.shirina);
				} else {
				deltaX = tmp.getPositionX()-selectCurFig.getPositionX();
			        }
		        if ((deltaX+deltaY) < deltaFig){


			volum=tmp; 
			deltaFig = deltaX+deltaY;

			}
			}
		       };
		       break;

		case 1:{//Вверх и влево

                     if( (tmp.getPositionY() < selectCurFig.getPositionY()) &&
                         (tmp.getPositionX() < selectCurFig.getPositionX())   ) {
		       if(Math.abs((selectCurFig.getPositionX() - tmp.getPositionX()) +
			           (selectCurFig.getPositionY() - tmp.getPositionY())) < deltaFig){
			volum=tmp;
			deltaFig= Math.abs((selectCurFig.getPositionX() - tmp.getPositionX()) +
					   (selectCurFig.getPositionY() - tmp.getPositionY()));
		       }
		       }
		      };
                      break;

		case 3:{//Вверх и вправо

                     if( (tmp.getPositionY() < selectCurFig.getPositionY()) &&
                         (tmp.getPositionX() > selectCurFig.getPositionX())   ) {
		       if(Math.abs((tmp.getPositionX() - selectCurFig.getPositionX()) +
			           (selectCurFig.getPositionY() - tmp.getPositionY())) < deltaFig){
			volum=tmp;
			deltaFig= Math.abs((tmp.getPositionX() - selectCurFig.getPositionX()) +
					   (selectCurFig.getPositionY() - tmp.getPositionY()));
		       }
		       }
		      };
                      break;

		case 7:{//вниз и влево

                     if( (tmp.getPositionY() > selectCurFig.getPositionY()) &&
                         (tmp.getPositionX() < selectCurFig.getPositionX())   ) {
		       if(Math.abs((selectCurFig.getPositionX() - tmp.getPositionX()) +
			           (tmp.getPositionY() - selectCurFig.getPositionY())) < deltaFig){
			volum=tmp;
			deltaFig= Math.abs((selectCurFig.getPositionX() - tmp.getPositionX()) +
					   (tmp.getPositionY() - selectCurFig.getPositionY()));
		       }
		       }
		      };
                      break;

		case 9:{//Вниз вправо

                     if( (tmp.getPositionY() > selectCurFig.getPositionY()) &&
                         (tmp.getPositionX() > selectCurFig.getPositionX())   ) {
		       if(Math.abs((tmp.getPositionX() - selectCurFig.getPositionX()) +
			           (tmp.getPositionY() - selectCurFig.getPositionY())) < deltaFig){
			volum=tmp;
			deltaFig= Math.abs((tmp.getPositionX() - selectCurFig.getPositionX()) +
					   (tmp.getPositionY() - selectCurFig.getPositionY()));
		       }
		       }
		      };
                      break;

	  }

	}

}


return volum;
}						 
						 
						 
public static void game(){

Pole.clear();
RecPlay.stateKey=0;
clearEvent();
timeEND = 255;
Figure F;
Figure.stado.setSize(0);


F= new Figure(0, 0, 0, Figure.curSpeed,0, false);
Figure.stado.addElement(F);


F= new Figure(0, 0, 0, Figure.curSpeed-1,0, false);
Figure.stado.addElement(F);


F= new Figure(0, 0, 0, Figure.curSpeed-2,0, false);
Figure.stado.addElement(F);



F= new Figure(0, 0, 0, Figure.curSpeed-3,0, false);
Figure.stado.addElement(F);

selectCurFig=(Figure)Figure.stado.elementAt(0);
}						 
						 
}