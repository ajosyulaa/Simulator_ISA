import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.Arrays;

class Pipeline2 {
    int[] codes;
    boolean[] completed;
    int result;
    String st;
    int mem_stall;
    int stall;
    int flag1;
    static int main_mem[][] = new int[32][8];
    static memory cache[][]= new memory[8][8];
    static int registers[]= new int[16];
    static { 
        // System.out.println("main");
        for(int i=0;i<main_mem.length;i++) {
            for(int j=0; j<main_mem[0].length; j++) {
                main_mem[i][j]= -1;
                // System.out.print(main_mem[i][j]+ " ");
            }
            // System.out.println();
        }
        // System.out.println("cache");
        for(int i=0; i<cache.length; i++) {
            for(int j=0; j<cache[0].length; j++) {
                cache[i][j] = new memory();
                cache[i][j].val = -1;
                cache[i][j].addr = -1;
                // System.out.print(cache[i][j].val+ " ");
            }
            // System.out.println();
        }
        // System.out.println("Registers");
        for(int i=0;i<registers.length;i++)
        {
            registers[i]=0;
            // System.out.print(registers[i]+ " ");
        }
        // System.out.println();
    }
    
    public Pipeline2() {
        codes=new int[7];
        for(int i=0;i<7; i++)
            codes[i]=-1;
        completed = new boolean[5];
        Arrays.fill(completed, Boolean.FALSE);
        result=0;
        st="";
        mem_stall=0;
        stall=0;
        flag1=-1;
    }

    void random_fill(int x) {
        List<Integer> rand_elements = new ArrayList<>();
        for (int i = 1; i <= x; i++) {
            rand_elements.add(i);
        }
        Collections.shuffle(rand_elements);
        for (int counter = 0; counter < rand_elements.size(); counter++) {              
            System.out.print(rand_elements.get(counter)+" " );
            write(rand_elements.get(counter), counter);        
        }
        System.out.println();
    }


    int read(int addr) {
        int block = (int)addr/main_mem[0].length;
        int offset = addr%main_mem[0].length;
        // System.out.println(block+" "+offset);
        for(int i=0; i<cache.length; i++) {    
            if(cache[i][offset].addr == addr) {
                // System.out.println(cache[i][offset].val+" "+cache[i][offset].addr);
                // System.out.println("Fetched from cache");
                flag1=0;
                return cache[i][offset].val;
            }
        }
        for(int i=0; i<main_mem.length; i++) {
            if(block==i) {
                int temp = i%cache.length;
                for (int j=0; j<cache[0].length; j++) {
                    cache[temp][j].val= main_mem[i][j];
                    cache[temp][j].addr = i*cache[0].length+j;
                }
                // System.out.println("Fetched from memory");
                flag1=1;
                return main_mem[i][offset];
            }
        }
        return -1;
    }

    void write(int val, int addr) {
        int block =0;
        int temp =0;
        block = (int)addr/main_mem[0].length;
        int offset = addr%main_mem[0].length;

        main_mem[block][offset]=val;
        // flag1=1;
        temp= block%cache.length;

        for (int j=0; j<cache[0].length; j++) {
            cache[temp][j].val=main_mem[block][j];
            cache[temp][j].addr=block*cache[0].length+j;
        }
    }

    void fetcher(int pc, String[] st_arr) {
        st=st_arr[pc];
        completed[0]=Boolean.TRUE;

    }
    
    void decoder() {
        String[] st_arr1 = st.split("\\s+");
        if(st_arr1[0].equals("LOAD")) {
                if(st_arr1[1].contains("[R")) {
                    codes[1]=4;
                }
                else if(st_arr1[1].contains("["))
                    codes[1]=0;
                else if(st_arr1[1].contains("R"))
                    codes[1]=1;
                else if(st_arr1[1].contains("#"))
                    codes[1]=2;
                else
                    codes[1]=3;

                if(st_arr1[2].contains("[R"))
                    codes[3]=4;
                else if(st_arr1[2].contains("["))
                    codes[3]=0;
                else if(st_arr1[2].contains("R"))
                    codes[3]=1;
                else if(st_arr1[2].contains("#"))
                    codes[3]=2;
                else
                    codes[3]=3;
                int reg = Integer.parseInt(st_arr1[1].replaceAll("[^0-9]", ""));
                int addr = Integer.parseInt(st_arr1[2].replaceAll("[^0-9]", ""));
                codes[0]=1;
                codes[2]=reg;
                codes[4]=addr;    
            }
            else if(st_arr1[0].equals("STORE")) {
                if(st_arr1[1].contains("[R"))
                    codes[1]=4;
                else if(st_arr1[1].contains("["))
                    codes[1]=0;
                else if(st_arr1[1].contains("R"))
                    codes[1]=1;
                else if(st_arr1[1].contains("#"))
                    codes[1]=2;
                else
                    codes[1]=3;

                if(st_arr1[2].contains("[R"))
                    codes[3]=4;
                else if(st_arr1[2].contains("["))
                    codes[3]=0;
                else if(st_arr1[2].contains("R"))
                    codes[3]=1;
                else if(st_arr1[2].contains("#"))
                    codes[3]=2;
                else
                    codes[3]=3;
                int addr = Integer.parseInt(st_arr1[1].replaceAll("[^0-9]", ""));
                int reg = Integer.parseInt(st_arr1[2].replaceAll("[^0-9]", ""));
                codes[0]=2;
                codes[2]=addr;
                codes[4]=reg;
            }
            else if(st_arr1[0].equals("ADD")) {
                if(st_arr1[1].contains("["))
                    codes[1]=0;
                else if(st_arr1[1].contains("R"))
                    codes[1]=1;
                else if(st_arr1[1].contains("#"))
                    codes[1]=2;
                else
                    codes[1]=3;

                if(st_arr1[2].contains("["))
                    codes[3]=0;
                else if(st_arr1[2].contains("R"))
                    codes[3]=1;
                else if(st_arr1[2].contains("#"))
                    codes[3]=2;
                else
                    codes[3]=3;

                if(st_arr1[3].contains("["))
                    codes[5]=0;
                else if(st_arr1[3].contains("R"))
                    codes[5]=1;
                else if(st_arr1[3].contains("#"))
                    codes[5]=2;
                else
                    codes[5]=3;
                int reg1 = Integer.parseInt(st_arr1[1].replaceAll("[^0-9]", ""));
                int reg2 = Integer.parseInt(st_arr1[2].replaceAll("[^0-9]", ""));
                int reg3 = Integer.parseInt(st_arr1[3].replaceAll("[^0-9]", ""));
                codes[0]=3;
                codes[2]=reg1;
                codes[4]=reg2;
                codes[6]=reg3;
            }
            else if(st_arr1[0].equals("SUB")) {
                if(st_arr1[1].contains("["))
                    codes[1]=0;
                else if(st_arr1[1].contains("R"))
                    codes[1]=1;
                else if(st_arr1[1].contains("#"))
                    codes[1]=2;
                else
                    codes[1]=3;

                if(st_arr1[2].contains("["))
                    codes[3]=0;
                else if(st_arr1[2].contains("R"))
                    codes[3]=1;
                else if(st_arr1[2].contains("#"))
                    codes[3]=2;
                else
                    codes[3]=3;

                if(st_arr1[3].contains("["))
                    codes[5]=0;
                else if(st_arr1[3].contains("R"))
                    codes[5]=1;
                else if(st_arr1[3].contains("#"))
                    codes[5]=2;
                else
                    codes[5]=3;
                int reg1 = Integer.parseInt(st_arr1[1].replaceAll("[^0-9]", ""));
                int reg2 = Integer.parseInt(st_arr1[2].replaceAll("[^0-9]", ""));
                int reg3 = Integer.parseInt(st_arr1[3].replaceAll("[^0-9]", ""));
                codes[0]=4;
                codes[2]=reg1;
                codes[4]=reg2;
                codes[6]=reg3;

            }
            else if(st_arr1[0].equals("CBNZ")) {
                if(st_arr1[1].contains("["))
                    codes[1]=0;
                else if(st_arr1[1].contains("R"))
                    codes[1]=1;
                else if(st_arr1[1].contains("#"))
                    codes[1]=2;
                else
                    codes[1]=3;

                if(st_arr1[2].contains("["))
                    codes[3]=0;
                else if(st_arr1[2].contains("R"))
                    codes[3]=1;
                else if(st_arr1[2].contains("#"))
                    codes[3]=2;
                else
                    codes[3]=3;
                int reg = Integer.parseInt(st_arr1[1].replaceAll("[^0-9]", ""));
                codes[4] = Integer.parseInt(st_arr1[2]);
                
                codes[0]=5;
                codes[2]=reg;
                
            }
            else if(st_arr1[0].equals("CBZ")) {
                if(st_arr1[1].contains("["))
                    codes[1]=0;
                else if(st_arr1[1].contains("R"))
                    codes[1]=1;
                else if(st_arr1[1].contains("#"))
                    codes[1]=2;
                else
                    codes[1]=3;

                if(st_arr1[2].contains("["))
                    codes[3]=0;
                else if(st_arr1[2].contains("R"))
                    codes[3]=1;
                else if(st_arr1[2].contains("#"))
                    codes[3]=2;
                else
                    codes[3]=3;
                int reg = Integer.parseInt(st_arr1[1].replaceAll("[^0-9]", ""));
                codes[4] = Integer.parseInt(st_arr1[2]);
                
                codes[0]=6;
                codes[2]=reg;
            }
            else if(st_arr1[0].equals("CMP")) {
                if(st_arr1[1].contains("["))
                    codes[3]=0;
                else if(st_arr1[1].contains("R"))
                    codes[3]=1;
                else if(st_arr1[1].contains("#"))
                    codes[3]=2;
                else
                    codes[3]=3;

                if(st_arr1[2].contains("["))
                    codes[5]=0;
                else if(st_arr1[2].contains("R"))
                    codes[5]=1;
                else if(st_arr1[2].contains("#"))
                    codes[5]=2;
                else
                    codes[3]=3;
                int reg1 = Integer.parseInt(st_arr1[1].replaceAll("[^0-9]", ""));
                int reg2 = Integer.parseInt(st_arr1[2].replaceAll("[^0-9]", ""));
                
                codes[0]=7;
                codes[4]=reg1;
                codes[6]=reg2;
            }
            else if(st_arr1[0].equals("JNG")) {
                if(st_arr1[1].contains("["))
                    codes[1]=0;
                else if(st_arr1[1].contains("R"))
                    codes[1]=1;
                else if(st_arr1[1].contains("#"))
                    codes[1]=2;
                else
                    codes[1]=3;
                codes[2] = Integer.parseInt(st_arr1[1]);
                codes[0]=8;
            // return codes
            }
    }

    void execute() {
        int reg1=0, reg2=0;
        switch(codes[0]) {
            case 1:
                if(codes[3]==0)
                    reg1=read(codes[4]);
                else if(codes[3]==1)
                    reg1=registers[codes[4]];
                else if(codes[3]==2)
                    reg1=codes[4];
                else if(codes[3]==4) {
                    reg1=read(registers[codes[4]]);
                }
                result=reg1;
                break;
            case 2:
                if(codes[3]==0)
                    reg1=read(codes[4]);
                else if(codes[3]==1)
                    reg1=registers[codes[4]];
                else if(codes[3]==2)
                    reg1=codes[4];
                result=reg1;
                break;
            case 3:
                if(codes[3]==0)
                    reg1=read(codes[4]);
                else if(codes[3]==1)
                    reg1=registers[codes[4]];
                else if(codes[3]==2)
                    reg1=codes[4];
                if(codes[5]==0)
                    reg2=read(codes[6]);
                else if(codes[5]==1)
                    reg2=registers[codes[6]];
                else if(codes[5]==2)
                    reg2=codes[6];
                result=reg1+reg2;
                break;
            case 4:
                if(codes[3]==0)
                    reg1=read(codes[4]);
                else if(codes[3]==1)
                    reg1=registers[codes[4]];
                else if(codes[3]==2)
                    reg1=codes[4];
                if(codes[5]==0)
                    reg2=read(codes[6]);
                else if(codes[5]==1)
                    reg2=registers[codes[6]];
                else if(codes[5]==2)
                    reg2=codes[6];
                result=reg1-reg2;
                break;
            case 5:
                if(codes[1]==0)
                    reg1=read(codes[2]);
                else if(codes[1]==1)
                    reg1=registers[codes[2]];
                else if(codes[1]==2)
                    reg1=codes[2];
                if(codes[3]==3)
                    reg2=codes[4];
                if(reg1!=registers[0])
                    result=reg2;
                else
                    result=-1;
                break;
            case 6:
                if(codes[1]==0)
                    reg1=read(codes[2]);
                else if(codes[1]==1)
                    reg1=registers[codes[2]];
                else if(codes[1]==2)
                    reg1=codes[2];
                if(codes[3]==3)
                    reg2=codes[4];
                if(reg1==registers[0])
                    result=reg2;
                else
                    result=-1;
                break;
            case 7:
                if(codes[3]==1)
                    reg1=registers[codes[4]];
                if(codes[5]==1)
                    reg2=registers[codes[6]];
                result=reg1-reg2;
                System.out.println(codes[6]+" "+registers[codes[6]]);
                System.out.println(result+"="+reg1+"-"+reg2);
                break;
            case 8:
                if(codes[1]==3)
                    reg1=codes[2];
                if(registers[14]<0)
                    result=reg1;
                else
                    result=-1;
                break;
        }

        completed[2]=Boolean.TRUE;
    }

    void memory_access() {
        if(flag1==0)
            mem_stall=3;
        if((flag1==1)||((codes[0]==2)&&(codes[1]==0)))
            mem_stall=100;
        if(mem_stall==0) {
            completed[3]=Boolean.TRUE;
        }
    }

    void write_back() {
        switch(codes[0]) {
            case 1:
                registers[codes[2]]=result;
                break;
            case 2:
                if(codes[1]==4)
                    write(result, registers[codes[2]]);
                else
                    write(result, codes[2]);
                break;
            case 3:
                // System.out.println("result "+result);
                registers[codes[2]]=result;
                break;
            case 4:
                registers[codes[2]]=result;
                break;
            case 5:
                if(result!=-1)
                    registers[13]=result-1;
                break;
            case 6:
                if(result!=-1)
                    registers[13]=result-1;
                break;
            case 7:
                System.out.println(result);
                registers[14]=result;
                break;
            case 8:
                if(result!=-1)
                    registers[13]=result-1;
                break;
        }
        completed[4]=Boolean.TRUE;
    }

    public static void main(String args[]) throws IOException {
        File file = new File("/home/akhila/Documents/spring20/CA/prog2.txt");
        BufferedReader br2 = new BufferedReader(new FileReader(file));
        String st;
        String[] st_arr= new String[17];
        int k=0;
        while ((st = br2.readLine()) != null) 
            st_arr[k++] = st;
        
        Pipeline2[] obj = new Pipeline2[st_arr.length];
        int timesteps[][] = new int[st_arr.length][2000000];

        for(int i=0;i<st_arr.length;i++) {
            obj[i]=new Pipeline2();
            for(int j=0;j<timesteps[0].length;j++) {
                timesteps[i][j]=-1;
            }
        }

        obj[0].random_fill(20);

        int time=0;
        int a=0;


        while(Boolean.TRUE) {
            System.out.println("-----------------------TIME------------------"+time);


            for(int i=a;i<st_arr.length;) {

                int control_flag=0;
                if(i>time) {
                    break;
                }
                if(obj[i].completed[4]) {
                    // System.out.println("Instruction "+i+": COMPLETED");
                    i++;
                    continue;
                }
                

                if (!obj[i].completed[0]) {
                    timesteps[i][time]=0;
                    obj[i].fetcher(i, st_arr);
                    System.out.println("Instruction "+i+": IF");
                    // System.out.println("0th");
                    break;
                }

                int org=i;
                if(obj[i].mem_stall>0) {
                    while(i<st_arr.length) {
                        // System.out.println("memory "+i);
                        if((i<=time)&&(timesteps[i][time]==-1))
                            timesteps[i][time]=timesteps[i][time-1];
                        i++;   
                    }

                    if(obj[org].mem_stall>0) {
                        // System.out.println(obj[org].mem_stall);
                        obj[org].mem_stall--;
                        System.out.println("Instruction "+org+": MEMORY STALLED "+obj[org].mem_stall);
                        if (obj[org].mem_stall==0) {
                            obj[org].completed[3]=Boolean.TRUE;

                        }
                    }
                    break;
                }

                if((timesteps[i][time-1]!=-1)&&(obj[i].completed[timesteps[i][time-1]])&&(obj[i].mem_stall==0)&&(obj[i].stall==0)) {

                    timesteps[i][time]=timesteps[i][time-1]+1;
                    
                    switch(timesteps[i][time]) {
                   
                        case 1:
                            obj[i].decoder();
                            for(int x1=0;x1<7;x1++)
                                System.out.print(obj[i].codes[x1]+" ");
                            System.out.println();
                            
                            for(int m=1; m<=2;m++) {
                                int prev = i-m;
                                if(((prev)>=0)&&(timesteps[prev][time]>=2)) {
                                    if(obj[i].codes[0]==1) {
                                        if(((obj[i].codes[3]==1)||(obj[i].codes[3]==4))&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[4]==obj[prev].codes[2])) {
                                            obj[i].stall = 4-timesteps[prev][time];
                                            // load op1                             
                                        }
                                        if(((obj[i].codes[1]==1)||(obj[i].codes[1]==4))&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[2]==obj[prev].codes[2])) {
                                            // if(obj[i].stall > (4-timesteps[prev][time]))
                                            obj[i].stall = 4-timesteps[prev][time];
                                            // load result;
                                        }
                                    }
                                    else if(obj[i].codes[0]==2) {
                                        if(((obj[i].codes[1]==1)||(obj[i].codes[1]==4))&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[4]==obj[prev].codes[2])) {   
                                            obj[i].stall = 4-timesteps[prev][time];
                                            // store op1;
                                        }
                                        if((obj[i].codes[1]==4)&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[2]==obj[prev].codes[2])) {
                                            obj[i].stall = 4-timesteps[prev][time];
                                            // store result;
                                        }    
                                    }
                                    else if(obj[i].codes[0]==3) {
                                        if((obj[i].codes[3]==1)&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[4]==obj[prev].codes[2])) {
                                            obj[i].stall = 4-timesteps[prev][time];
                                            // add op1;
                                        }
                                        if((obj[i].codes[5]==1)&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[6]==obj[prev].codes[2])) {
                                            obj[i].stall = 4-timesteps[prev][time];
                                            // add op2;
                                        }
                                    }
                                    else if(obj[i].codes[0]==4) {
                                        if((obj[i].codes[3]==1)&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[4]==obj[prev].codes[2])) {
                                            obj[i].stall = 4-timesteps[prev][time];
                                            // sub op1;
                                        }
                                        if((obj[i].codes[5]==1)&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[6]==obj[prev].codes[2])) {
                                            obj[i].stall = 4-timesteps[prev][time];
                                            // sub op2;
                                        }
                                    }
                                    else if((obj[i].codes[0]==5)||(obj[i].codes[0]==6)) {
                                        if((obj[i].codes[3]==1)&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[4]==obj[prev].codes[2])) {
                                            obj[i].stall = 4-timesteps[prev][time];
                                            // cbnz and cbz op1;
                                        }
                                    }
                                    else if(obj[i].codes[0]==7) {
                                        if((obj[i].codes[3]==1)&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[4]==obj[prev].codes[2])) {
                                            if(obj[i].stall<(4-timesteps[prev][time]))
                                            obj[i].stall = 4-timesteps[prev][time];
                                            // cmp op1;
                                        }
                                        if((obj[i].codes[5]==1)&&((obj[prev].codes[0]<=4)&&((obj[prev].codes[1]==1)||(obj[prev].codes[1]==4)))&&(obj[i].codes[6]==obj[prev].codes[2])) {
                                            obj[i].stall = 4-timesteps[prev][time];
                                            
                                        }
                                    }
                                    else if((obj[i].codes[0]==8)&&(obj[i].codes[0]==7)) {
                                        obj[i].stall = 4-timesteps[prev][time];
                                    }  
                                }
                            }
                            

                            if(obj[i].stall>0)
                                break;
                            

                            obj[i].completed[1]=Boolean.TRUE;
                            System.out.println("Instruction "+i+": ID");
                            // System.out.println("1st");

                            break;
                        case 2:
                            obj[i].execute();
                            System.out.println("Instruction "+i+": EX");
                            // System.out.println("2nd");

                            break;
                        case 3:
                            obj[i].memory_access();
                            System.out.println("Instruction "+i+": MEM");
                            // System.out.println("3rd");

                            break;
                        case 4:
                            obj[i].write_back();
                            if(((obj[i].codes[0]==5)||(obj[i].codes[0]==6)||(obj[i].codes[0]==8))&&(obj[i].result!=-1)) {
                                control_flag=1;
                            }
                            System.out.println("Instruction "+i+": WB");
                            // System.out.println("4th");
                            break;
                    }
                    
                }

                if(control_flag==1) {
                    for(int l=registers[13]; l<st_arr.length; l++)
                        obj[l]=new Pipeline2();
                    i = registers[13]-1;
                    a=registers[13];
                    i++;
                    registers[13]=i;
                    break;
                }

              
                if(obj[i].stall>0) {

                    while(i<st_arr.length) {
                        if((i<=time)&&(timesteps[i][time]==-1))
                            timesteps[i][time]=timesteps[i][time-1];
                        i++;   
                    }

                    if(obj[org].stall>0) {
                        obj[org].stall--;
                        System.out.println("Instruction "+org+": DATA STALLED "+obj[org].stall);

                        if (obj[org].stall==0) {
                            obj[org].completed[1]=Boolean.TRUE;
                        }
                    }

                }
                else {
                    i++;
                    
                }
                if(control_flag==0)
                    a=0;
                registers[13]=i;
            }

            int count1=0;
            for(int i1=0;i1<st_arr.length;i1++) {
                for(int j1=0;j1<5;j1++) {
                    if(obj[i1].completed[j1]) {
                        count1++;
                    }
                }
            }
            System.out.println("main");
            for(int i=0;i<main_mem.length;i++) {
                for(int j=0; j<main_mem[0].length; j++) {

                    System.out.print(main_mem[i][j]+ " ");
                }
                System.out.println();
            }
            System.out.println("cache");
            for(int i=0; i<cache.length; i++) {
                for(int j=0; j<cache[0].length; j++) {
                    
                    System.out.print(cache[i][j].val+ " ");
                }
                System.out.println();
            }
            System.out.println("Registers");
            for(int i=0;i<registers.length;i++)
            {
                System.out.print(registers[i]+ " ");
            }
            System.out.println();

            if(count1==st_arr.length*5)
                break;
            time++;
            if((registers[2]==-1)&&(registers[1]==-1)) {
                break;
            }

        } 
    } 
}