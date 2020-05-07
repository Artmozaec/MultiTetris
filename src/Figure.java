import java.util.*;

public class Figure implements Const {
int positionX, positionY, set;
public boolean[][] fig;
public int speedTime, evNum;
public static Vector stado;
private static Random rnd = new Random();
public int visota, shirina, rotatePos;
public static int curSpeed;


static{
stado = new Vector();
curSpeed = 10;
}


public static void stadoSyn(){
	for (int v=0; v<Figure.stado.size(); v++){
	 ((Figure)Figure.stado.elementAt(v)).evNum=0;
	}
}


public static int ranDom(int val){
return Math.abs(rnd.nextInt() % val);
}

Figure (int setIn, int px, int py, int ST, int rot, boolean keyIn){

int v;
 speedTime=ST; 
 rotatePos=0;
 if (keyIn) set=setIn; else set = ranDom(6);
   switch  (set) {

   case 0: fig = new boolean [][]{
    {true}, // |
    {true},
    {true},
    {true},
    };

   break;
    
   case 1: fig = new boolean [][]{
    {true ,false,false}, //|___
    {true ,true ,true }, 
    };

   break;


   case 2: fig = new boolean [][]{  
    {false,false,true},//___|
    {true ,true ,true},
    };
 
   break;

   case 3: fig = new boolean [][]{
    {true ,true}, //[]
    {true ,true},
    };

   break;

   case 4: fig = new boolean [][]{
    {false,true,false},//_|_
    {true ,true, true},
    };
 
   break;

   case 5: fig = new boolean [][]{
    {true ,true,false}, //Z
    {false,true,true },
    };
 
   break;


  case 6: fig = new boolean [][]{
    {false,true ,true }, //S
    {true ,true ,false},
    };

  break;

 }
 
 for (;rot>0; rot--) rotate(false);
 
 shirina  = fig[0].length*Gl.kf;
 visota = fig.   length*Gl.kf;
 if (keyIn) positionX = px; else positionX=(ranDom(10-fig[0].length))*Gl.kf;
 if (keyIn) positionY= py; else positionY=0;

 v=checkMove(positionX, positionY, fig);
 if (v==-4){ TaktSyn.pauseKey=true;  TaktSyn.gameOverFlag = true; return; }
 while ( v > -1 ){ 
  //System.out.println("Внутри конструктора, рожденная фигура натолкнулась на фигуру - "+v);
  ((Figure)stado.elementAt(v)).move(A_DOWN, Gl.kf);
  v=checkMove(positionX, positionY, fig);
  evNum=0;
  }
}

private Figure upSelect(){
Figure F;
F=this;
int delta = 300;

for (int v=0; v<stado.size(); v++){
        //System.out.println("upSelect Идёт поиск перемещения выделения - текущий элемент = "+v);
	if (((Math.abs(positionY-((Figure)stado.elementAt(v)).getPositionY())+
	     Math.abs(positionX-((Figure)stado.elementAt(v)).getPositionX()) ) < delta) && (((Figure)stado.elementAt(v)) != this)) 
		{
	    delta = (Math.abs(positionY-((Figure)stado.elementAt(v)).getPositionY())+
	             Math.abs(positionX-((Figure)stado.elementAt(v)).getPositionX()));
	    F=      ((Figure)stado.elementAt(v));             
		} 

                                  }
return F;
}

public void perenos(){
int k2k;
Figure f;
    for (int y=0; y < fig.length; y++){
		for (int x=0; x < fig[0].length; x++){
			if (fig[y][x]) { Pole.set((((y*Gl.kf)+positionY)/Gl.kf),(((x*Gl.kf)+positionX)/Gl.kf), true); }
		}
   }
   
if (TaktSyn.selectCurFig == this) TaktSyn.selectCurFig = upSelect();

  stado.removeElement(this);
  int X1 = Pole.fullLine();
  if (X1>0) {
			Gl.resultL+=10*X1;
			if ((curSpeed>3) && (Gl.resultL%21 == 11)) curSpeed--;
			//System.out.println("curSpeed = "+ curSpeed + "Gl.resultL%21 = "+ (Gl.resultL%21));
							 
			}
  if (RecPlay.stateKey != RecPlay.PLAY) {
	k2k = 3-Figure.stado.size() - (Pole.getLine()/60);

	for (int x=0; ((x<k2k) && (!TaktSyn.gameOverFlag)); x++) {
		TaktSyn.addEvent(10);
    }

   }
	
}

public int checkMove(int newPositionX, int newPositionY, boolean[][] mas){
Figure [] FF;
int nvisota = mas.length*Gl.kf,
    nshirina= mas[0].length*Gl.kf;

if (newPositionY+nvisota>Pole.visota){ 
//System.out.println("Низ!");
return -2; // Нижняя линия
}

if ((newPositionX<0) || (newPositionX+nshirina>Pole.shirina)) return -3;//выход за правую или левую границу

 FF = new Figure[3];
 int id=0;
 Gl.testKey = false;
 
 for (int v=0; v<stado.size(); v++){
   if (   (((Figure)stado.elementAt(v)) != this) &&  

          ((newPositionY+nvisota)-((Figure)stado.elementAt(v)).getPositionY() >0 ) &&

          (  (newPositionY+nvisota)  - (  ((Figure)stado.elementAt(v)).getPositionY() + ((Figure)stado.elementAt(v)).visota) < nvisota ) && 

          ((newPositionX+nshirina) -  ((Figure)stado.elementAt(v)).getPositionX() >0 ) && 

          ((newPositionX+nshirina) - (((Figure)stado.elementAt(v)).getPositionX() + ((Figure)stado.elementAt(v)).shirina)) < nshirina) {

       FF[id] = ((Figure)stado.elementAt(v));

	   Gl.testKey = true;
	   
       id++;
   }
      
 }


 int npY=(newPositionY/Gl.kf);
 int npX=(newPositionX/Gl.kf);
 
 boolean posKey=true;
 if ((newPositionY)-(npY*Gl.kf) ==0 ) posKey=false;
 
 for (int y=0; y<mas.length; y++){
  for (int x=0; x<mas[0].length; x++){

     if ( (mas[y][x]) && 
			( ((!posKey) && (   Pole.get((y+npY),(x+npX))        )) || 
			  ((posKey) && ((   Pole.get((y+npY),(x+npX))         ) || (      Pole.get((y+npY+1),(x+npX))         ))))) return -4;
			  
    for (int v=0; v<id; v++){

	    for (int ys=0; ys<FF[v].fig.length; ys++){
		for (int xs=0; xs<FF[v].fig[0].length; xs++){
	         if  (((FF[v].fig[ys][xs]) && (mas[y][x])) &&
		         (  (newPositionY+((y+1)*Gl.kf)) - (FF[v].getPositionY()+(ys*Gl.kf)) > 0    ) &&
				 (  (newPositionY+((y+1)*Gl.kf)) - (FF[v].getPositionY()+((ys+1)*Gl.kf)) < Gl.kf) && 
	             (  (newPositionX+((x+1)*Gl.kf)) - (FF[v].getPositionX()+(xs*Gl.kf)) > 0    ) &&
				 (  (newPositionX+((x+1)*Gl.kf)) - (FF[v].getPositionX()+((xs+1)*Gl.kf)) < Gl.kf) )
	             {
				 return stado.indexOf(FF[v]);}
		}
	     }
   }
  }
 }

return -1;
}


public boolean rotate(boolean rKey){


boolean[][] fig2;
int figLX, figLY, v;

   fig2 = new boolean[fig[0].length][fig.length];
   
   figLX =0;


   for (int y=0; y < fig2.length; y++){
    figLY=fig.length-1;

    for (int x=0; x < fig2[0].length; x++){

     fig2[y][x]= fig[figLY][figLX];
     figLY--;
    }
    figLX++;
   }
    if (rKey) {
		v=checkMove(positionX, positionY, fig2);   

		if ((v==-4) || (v==-2))  return false;


		while (v==-3) {
			if (positionX<5) {if (!move(A_RIGHT, Gl.kf)) return false;
							} else {
							if (!move(A_LEFT, Gl.kf)) return false;
							}
		v=checkMove(positionX, positionY, fig2);
       } 


		while ( v > -1 ){ 

			if (positionY<((Figure)stado.elementAt(v)).getPositionY()) {

					//System.out.println("При повороте фигура наткнулась на другую фигуру и находится от неё Выше.");
					if ( !((Figure)stado.elementAt(v)).move(A_DOWN, Gl.kf) ) return false;
	    
		}else{

			if (positionX<((Figure)stado.elementAt(v)).getPositionX()){ // Текущая фигура с лева
					//System.out.println("При повороте фигура наткнулась на другую фигуру и находится от неё с Лево");
					if ( !((Figure)stado.elementAt(v)).move(A_RIGHT, Gl.kf) ){ if (!move(A_DOWN, Gl.kf)) return false;}
			}
			if (positionX>((Figure)stado.elementAt(v)).getPositionX()){
					//System.out.println("При повороте фигура наткнулась на другую фигуру и находится от неё с Право");
					if ( !((Figure)stado.elementAt(v)).move(A_LEFT, Gl.kf) ){ if (!move(A_DOWN, Gl.kf)) return false;}
			}
			if (positionX==((Figure)stado.elementAt(v)).getPositionX()){
					//System.out.println("При повороте фигура наткнулась на другую фигуру и находится от неё середина");
					if (!move(A_DOWN, Gl.kf)) return false;
			}
		  }
		   v=checkMove(positionX, positionY, fig2);

		}
	}
   
   fig=fig2;
   shirina = fig[0].length*Gl.kf;
   visota = fig.length*Gl.kf;
   if (rotatePos<3) rotatePos++; else rotatePos=0;
   
return true;

};

public boolean move(int key, int val){
int v;
boolean ee;
ee = false;

 if (val==1) { if (evNum<speedTime) {evNum++; key=100;} //Если evNum не набрал нужного колличесва досвидания!
			   else evNum=0; }
 switch (key) {
   case A_DOWN: {
    
	       v=checkMove(positionX, positionY+val, fig);
	       switch (v){
		case -1:{ //движение свободно
   
			  positionY+=val;
			  return true;
			}
		
		case -4:{
			
				if (positionY-((positionY/Gl.kf)*Gl.kf) != 0) positionY += Gl.kf;
																		
				} // колодец
		
		case -2:{ //Конецъ движения нижняя линия
 			 
                          if (v==-2) positionY=Pole.visota-visota;
                          perenos();
						 
			  return false;
			}

	       default: {
		          do{
				    if (!((Figure)stado.elementAt(v)).move(A_DOWN, val)) return false;
                    v=checkMove(positionX, positionY+val, fig);
					}
				  while (v>=0);
				  positionY+=val; ee=true;
	                };
 	        }

   };
   break;
  case A_LEFT: {
              v=checkMove(positionX-val, positionY, fig);
	      switch (v){
	       case -1:{ //движение свободно
			positionX-=val;
			return true;
		       }
	       case -4: {}
	       case -3: return false; //стена или колодец

	       default: {
					do  {
					    if (!((Figure)stado.elementAt(v)).move(A_LEFT, val)) return false;
						v=checkMove(positionX-val, positionY, fig);
						}
					while (v>=0);
					positionX-=val; ee=true;
	                };

	      }

  };
  break;
  case A_RIGHT:{
	      v=checkMove(positionX+val, positionY, fig);
	      switch (v){
	       case -1:{ //движение свободно
			positionX+=val;
			return true;
		       }
	       case -4: {}
	       case -3: return false; //стена или 

	       default:{
		          do { 
				     if (!((Figure)stado.elementAt(v)).move(A_RIGHT, val)) return false; 
					 v=checkMove(positionX+val, positionY, fig);					 
					 }
                  while (v>=0);
				  positionX+=val; ee=true;
	                };

	      }
  };
 
 }
 return ee;
}

public int getPositionX(){
return positionX;
}

public int getPositionY(){
return positionY;
}




}

