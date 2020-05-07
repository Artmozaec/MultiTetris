import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import java.io.*;
import java.util.*;


public class Gl extends Canvas implements Const{

private final int RED   = 0xFF0000;
private final int BLUE  = 0x0000FF;
private final int WHITE = 0xFFFFFF;
private final int GREEN = 0x00FF00;
private final int BLACK = 0x000000;


public static int widht, height, ky;
public String str;

/*static {
key=54;
}*/


int ii = 50;
public static int kf, resultL;
public static boolean testKey;
public RecPlay RP;


Gl(){
 widht =getWidth();
 height =getHeight();
 kf = height/20;
 Pole.visota  = kf*Pole.kolodec.length;    //Вычисляется здесь, потому-что kf становится известен здесь
 Pole.shirina = kf*Pole.kolodec[0].length;
}



public void paint(Graphics G){

          G.setColor(WHITE);
          G.fillRect(0,0,G.getClipWidth(),G.getClipHeight());
          G.translate(4,5);
	  

          G.setColor(RED);
          for(int i=0; i<=kf*20; i+=kf) G.drawLine(0,i,kf*10,i);
          for(int i=0; i<=kf*10; i+=kf) G.drawLine(i,0,i,kf*20);

	  // КОЛОДЕЦ

            G.setColor(BLUE);
	   for(int y=0; y<20; y++){
            for(int x=0; x<10; x++){
	     if (Pole.get(y,x)) {
               G.setColor(BLUE);
              } else {
	       G.setColor(WHITE);
              }
	       G.fillRect(x*kf+1, y*kf+1, kf-1, kf-1);
	    }
	   }  
       
          //Отображение фигуры

          G.setColor(RED);
          for (int v=0; v<Figure.stado.size(); v++){

	  for (int y=0; y<((Figure)Figure.stado.elementAt(v)).fig.length; y++){
	      for (int x=0; x<((Figure)Figure.stado.elementAt(v)).fig[y].length; x++){

		if (((Figure)Figure.stado.elementAt(v)).fig[y][x]) 

                    G.fillRect((((Figure)Figure.stado.elementAt(v)).getPositionX()+(x*kf))+1, 
                   (((Figure)Figure.stado.elementAt(v)).getPositionY()+(y*kf))+1, kf-1, kf-1);
	      }
	   }
           }
	  
         // Отображение выделения
          if (!TaktSyn.selectFig){
          G.setColor(GREEN);
	  for (int y=0; y<TaktSyn.selectCurFig.fig.length; y++){
	      for (int x=0; x<TaktSyn.selectCurFig.fig[y].length; x++){
 		if (TaktSyn.selectCurFig.fig[y][x]) 

                    G.drawRect((TaktSyn.selectCurFig.getPositionX()+(x*kf))+1, 
                   (TaktSyn.selectCurFig.getPositionY()+(y*kf))+1, kf-1, kf-1);
	      }
	   }
           } else {
	   G.setColor(GREEN);          
	  for (int y=0; y<TaktSyn.selectCurFig.fig.length; y++){
	      for (int x=0; x<TaktSyn.selectCurFig.fig[y].length; x++){
 		if (TaktSyn.selectCurFig.fig[y][x]) 

                    G.fillRect((TaktSyn.selectCurFig.getPositionX()+(x*kf))+1, 
                   (TaktSyn.selectCurFig.getPositionY()+(y*kf))+1, kf-1, kf-1);
	      }
	   }
           }
	  
    if (testKey) G.fillRect(kf*12, kf*5, 20, 20);
	if (RecPlay.stateKey == RecPlay.RECORD) G.drawString("R",kf*12,kf*8,G.HCENTER|G.TOP);
	if (RecPlay.stateKey == RecPlay.PLAY) G.drawString("P",kf*12,kf*8,G.HCENTER|G.TOP);
	G.setColor(BLACK);
	G.drawString(" "+resultL,(kf*10)+2,kf,G.LEFT|G.TOP);
}



public void keyPressed(int keyCode) {
if (((keyCode>48) && (keyCode<=57)) || (keyCode==42) || (keyCode==35)){ 

  if (RecPlay.stateKey == 2) {RecPlay.stateKey=0; RecPlay.curClose();}
   TaktSyn.addEvent(keyCode);
}
}	
 
}