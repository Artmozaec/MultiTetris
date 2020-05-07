public class Pole{

public static boolean [][] kolodec;
public static int lines, visota, shirina;

static {
kolodec = new boolean[20][10];

}

public static void clear(){
for (int y=0; y<20; y++){
    for (int x=0; x<10; x++){
      kolodec[y][x] = false;
    }
}
}


private static void clearLine(int met){
lines++;
for(int y=met; y>0; y--){
  for(int x=0; x<10; x++){
     kolodec[y][x] = kolodec[y-1][x];
  }
}
}


public static int getLine(){
int ch=0;

for (int y=0; y<20; y++){
    for (int x=0; x<10; x++){
      if (kolodec[y][x]) ch++;
    }
}

return ch;
   
}

public static int fullLine(){
int ch, lineClean;

lineClean=0;
for (int y=0; y<20; y++){
    ch=-1;
    for (int x=0; x<10; x++){
     if (kolodec[y][x]) ch++;
    }
    if (ch==9) {clearLine(y); lineClean++;}
}

return lineClean;
}

public static boolean get(int y, int x){
return kolodec[y][x];
}

public static void set(int y, int x, boolean val){
kolodec[y][x]=val;
}




}