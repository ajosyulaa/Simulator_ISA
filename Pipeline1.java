import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.Arrays;

class Pipeline1 {
    int[] codes;
    boolean[] completed;
    int result;
    String st;
    int mem_stall;
    int stall;
    int flag1;
    static int main_mem[][] = new int[16][8];
    static memory cache[][]= new memory[4][8];
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
    
    public Pipeline1() {
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
        // System.out.println(block+" "+offset);

        main_mem[block][offset]=val;
        // flag1=1;
        temp= block%cache.length;

        for (int j=0; j<cache[0].length; j++) {
            cache[temp][j].val=main_mem[block][j];
            cache[temp][j].addr=block*cache[0].length+j;
            // System.out.println(+cache[temp][j].val+" "+cache[temp][j].addr);
        }
        // System.out.println("Written to cache");   
    }

    void fetcher(int pc, String[] st_arr) {
        // System.out.print("here1 "+ pc);
        st=st_arr[pc];
        // System.out.print("here2 "+ st);
        completed[0]=Boolean.TRUE;
        // System.out.print("here3 "+ completed[0]);

    }
    
    void decoder() {
        String[] st_arr1 = st.split("\\s+");
        if(st_arr1[0].equals("LOAD")) {
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
                int addr = Integer.parseInt(st_arr1[2].replaceAll("[^0-9]", ""));
                // System.out.println(st_arr1[0]+" "+reg+" "+addr);
                codes[0]=1;
                codes[2]=reg;
                codes[4]=addr;    
            }
            else if(st_arr1[0].equals("STORE")) {
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
                int addr = Integer.parseInt(st_arr1[1].replaceAll("[^0-9]", ""));
                int reg = Integer.parseInt(st_arr1[2].replaceAll("[^0-9]", ""));
                // System.out.println(st_arr1[0]+" "+reg+" "+addr);
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
                // System.out.println(st_arr1[0]+" "+reg1+" "+reg2+" "+reg3);
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
                // System.out.println(st_arr1[0]+" "+reg1+" "+reg2+" "+reg3);
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
            // return codes
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
                // System.out.println(result+"="+reg1+"-"+reg2);
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
        }
        // return result;
        // System.out.println("operation"+result+" "+reg1+" "+reg2);
        completed[2]=Boolean.TRUE;
    }

    void memory_access() {
        if(flag1==0)
            mem_stall=40;
        if((flag1==1)||((codes[0]==2)&&(codes[1]==0)))
            mem_stall=100;
        if(mem_stall==0) {
            completed[3]=Boolean.TRUE;
            // System.out.println("HEREEEEE");
        }
    }

    void write_back() {
        switch(codes[0]) {
            case 1:
                registers[codes[2]]=result;
                break;
            case 2:
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
                    registers[13]=result;
                break;
        }
        completed[4]=Boolean.TRUE;
    }

    public static void main(String args[]) throws IOException {
        File file = new File("/home/akhila/Documents/spring20/CA/prog.txt");
        BufferedReader br2 = new BufferedReader(new FileReader(file));
        String st;
        String[] st_arr= new String[6];
        int k=0;
        while ((st = br2.readLine()) != null) 
            st_arr[k++] = st;
        
        Pipeline1[] obj = new Pipeline1[st_arr.length];
        int timesteps[][] = new int[st_arr.length][400];
        // for(int i2=0;i2<st_arr.length;i2++) {
        //     System.out.println(st_arr[i2]+" "+);
        // }
        for(int i=0;i<st_arr.length;i++) {
            obj[i]=new Pipeline1();
            for(int j=0;j<timesteps[0].length;j++) {
                timesteps[i][j]=-1;
            }
        }
        // obj[0].write(5, 0);
        main_mem[0][0]=5;
        // cache[0][0].val=5;
        // cache[0][0].addr=0;

        int time=0;
        int a=0;
        
        while(Boolean.TRUE) {
            System.out.println("-----------------------TIME------------------"+time);
            // for(int i2=0;i2<st_arr.length;i2++) {
            //         for(int j2=0;j2<timesteps[0].length;j2++) {
            //             System.out.print(timesteps[i2][j2]+ " ");
            //         }
            //         System.out.println();
            //     }

            for(int i=a;i<st_arr.length;) {
                // System.out.println("i: "+i);

                // for(int i2=0;i2<5;i2++)
                //     System.out.print(obj[i].completed[i2]+ " ");
                // System.out.println();
                int control_flag=0;
                if(i>time) {
                    break;
                }
                if(obj[i].completed[4]) {
                    // System.out.println("Instruction "+i+": COMPLETED");
                    i++;
                    continue;
                }
                // int prev=0, count=0;
                // System.out.print(i+" ");
                // for(int i2=0;i2<5;i2++)
                //     System.out.print(obj[i].completed[i2]+ " ");
                // System.out.println();
                

                if (!obj[i].completed[0]) {
                    timesteps[i][time]=0;
                    obj[i].fetcher(i, st_arr);
                    System.out.println("Instruction "+i+": IF");
                    // System.out.println("0th");
                    break;
                }
                // System.out.println(i);
                // for(int i2=0;i2<st_arr.length;i2++) {
                //         for(int j2=0;j2<400;j2++) {
                //             System.out.print(timesteps[i2][j2]+ " ");
                //         }
                //         System.out.println();
                //     }
                // System.out.print(i+" ");
                // for(int i2=0;i2<5;i2++)
                //     System.out.print(obj[i].completed[i2]+ " ");
                // System.out.println();
                // System.out.println(i+" "+time+" "+timesteps[i][time-1]+" "+obj[i].completed[timesteps[i][time-1]]+" "+obj[i].mem_stall+" "+obj[i].mem_stall);

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
                    // System.out.println(obj[org].mem_stall+" here "+i);
                }

                if((timesteps[i][time-1]!=-1)&&(obj[i].completed[timesteps[i][time-1]])&&(obj[i].mem_stall==0)&&(obj[i].stall==0)) {
                    // System.out.println("timestep update "+timesteps[i][time]+" "+timesteps[i][time-1]);

                    timesteps[i][time]=timesteps[i][time-1]+1;
                    // System.out.println("timestep update "+timesteps[i][time]+" "+timesteps[i][time-1]);
                    
                    switch(timesteps[i][time]) {
                        // case 1: 
                        //     // System.out.println("here");
                        //     obj[i].fetcher(i, st_arr);
                        //     System.out.println("Instruction "+i+": IF");
                        //     break;
                        case 1:
                            obj[i].decoder();
                            // if(time==2)
                            // System.out.print("codes- ");
                            for(int x1=0;x1<7;x1++)
                                System.out.print(obj[i].codes[x1]+" ");
                            System.out.println();
                            if((i-2)>=0) {
                                if(((obj[i].codes[0]==1)||(obj[i].codes[0]==2))&&((obj[i-2].codes[0]==1)||(obj[i-2].codes[0]==3)||(obj[i-2].codes[0]==4))) {
                                    if((obj[i].codes[3]==1)&&(obj[i].codes[4]==obj[i-2].codes[2])&&(timesteps[i-2][time]>=2)) {
                                        obj[i].stall = 4-timesteps[i-2][time];
                                        // break;
                                    }
                                }
                                else if(((obj[i].codes[0]==3)||(obj[i].codes[0]==4))&&((obj[i-2].codes[0]==1)||(obj[i-2].codes[0]==3)||(obj[i-2].codes[0]==4))) {
                                    if((obj[i].codes[3]==1)&&(obj[i].codes[4]==obj[i-2].codes[2])&&(timesteps[i-2][time]>=2)) {
                                        obj[i].stall = 4-timesteps[i-2][time];
                                        // break;
                                    }
                                    if((obj[i].codes[5]==1)&&(obj[i].codes[6]==obj[i-2].codes[2])&&(timesteps[i-2][time]>=2)) {
                                        obj[i].stall = 4-timesteps[i-2][time];
                                        // break;
                                    }
                                }
                                else if(((obj[i].codes[0]==5)||(obj[i].codes[0]==6))&&((obj[i-2].codes[0]==1)||(obj[i-2].codes[0]==3)||(obj[i-2].codes[0]==4))) {
                                    if((obj[i].codes[1]==1)&&(obj[i].codes[2]==obj[i-2].codes[2])&&(timesteps[i-2][time]>=2)) {
                                        obj[i].stall = 4-timesteps[i-2][time];
                                        // break;
                                    }
                                }
                            }
                            // System.out.println("here"+(i-1));
                            if((i-1)>=0) {
                                // System.out.println(obj[i].codes[0]+" "+obj[i].codes[0]+" "+obj[i-1].codes[0]+" "+obj[i-1].codes[0]+" "+obj[i-1].codes[0]);
                                if(((obj[i].codes[0]==1)||(obj[i].codes[0]==2))&&((obj[i-1].codes[0]==1)||(obj[i-1].codes[0]==3)||(obj[i-1].codes[0]==4))) {
                                    if((obj[i].codes[3]==1)&&(obj[i-1].codes[1]==1)&&(obj[i].codes[4]==obj[i-1].codes[2])&&(timesteps[i-1][time]>=2)) {
                                        obj[i].stall = 4-timesteps[i-1][time];
                                        // break;
                                    }
                                }
                                else if(((obj[i].codes[0]==3)||(obj[i].codes[0]==4))&&((obj[i-1].codes[0]==1)||(obj[i-1].codes[0]==3)||(obj[i-1].codes[0]==4))) {
                                    System.out.println("here");
                                    if((obj[i].codes[3]==1)&&(obj[i-1].codes[1]==1)&&(obj[i].codes[4]==obj[i-1].codes[2])&&(timesteps[i-1][time]>=2)) {
                                        obj[i].stall = 4-timesteps[i-1][time];
                                        System.out.println("hereeee");
                                        // break;
                                    }
                                    // System.out.println((obj[i].codes[5]==1)+" "+(obj[i-1].codes[1]==1)+" "+(obj[i].codes[6]==obj[i-1].codes[2])+" "+(timesteps[i-1][time]>2));
                                    if((obj[i].codes[5]==1)&&(obj[i-1].codes[1]==1)&&(obj[i].codes[6]==obj[i-1].codes[2])&&(timesteps[i-1][time]>=2)) {
                                        obj[i].stall = 4-timesteps[i-1][time];
                                        // System.out.println(obj[i].stall);
                                        // break;
                                    }
                                }
                                else if(((obj[i].codes[0]==5)||(obj[i].codes[0]==6))&&((obj[i-1].codes[0]==1)||(obj[i-1].codes[0]==3)||(obj[i-1].codes[0]==4))) {
                                    if((obj[i].codes[1]==1)&&(obj[i].codes[2]==obj[i-1].codes[2])&&(timesteps[i-1][time]>=2)) {
                                        obj[i].stall = 4-timesteps[i-1][time];
                                        // break;
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
                            // set memory access delay
                            
                            obj[i].memory_access();
                            System.out.println("Instruction "+i+": MEM");
                            // System.out.println("3rd");

                            break;
                        case 4:
                            obj[i].write_back();
                            if(((obj[i].codes[0]==5)||(obj[i].codes[0]==6))&&(obj[i].result!=-1))
                                control_flag=1;
                            System.out.println("Instruction "+i+": WB");
                            // System.out.println("4th");
                            break;
                    }
                    
                }

                // else
                    // timesteps[i][time]=timesteps[i][time-1];
                if(control_flag==1) {
                    for(int l=registers[13]; l<st_arr.length; l++)
                        obj[l]=new Pipeline1();
                    i = registers[13]-1;
                    a=registers[13];
                }

                // int mem_flag=0;
                // for(int g=0; g<st_arr.length;g++) {
                //     if(obj[g].mem_stall>0) {
                //         mem_flag=1;
                //         break;
                //     }
                // }
                if(obj[i].stall>0) {

                    while(i<st_arr.length) {
                        // System.out.println("data "+i);
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

                    // System.out.println(obj[org].mem_stall+" here "+i);
                }
                else {
                    // System.out.println("here1");
                    i++;
                }
                registers[13]=i;
            }
            int count1=0;
            for(int i1=0;i1<st_arr.length;i1++)
                for(int j1=0;j1<5;j1++)
                    if(obj[i1].completed[j1])
                        count1++;
        
            System.out.println("main");
            for(int i=0;i<main_mem.length;i++) {
                for(int j=0; j<main_mem[0].length; j++) {
                    // main_mem[i][j]= -1;
                    System.out.print(main_mem[i][j]+ " ");
                }
                System.out.println();
            }
            System.out.println("cache");
            for(int i=0; i<cache.length; i++) {
                for(int j=0; j<cache[0].length; j++) {
                    // cache[i][j] = new memory();
                    // cache[i][j].val = -1;
                    // cache[i][j].addr = -1;
                    System.out.print(cache[i][j].val+ " ");
                }
                System.out.println();
            }
            System.out.println("Registers");
            for(int i=0;i<registers.length;i++)
            {
                // registers[i]=0;
                System.out.print(registers[i]+ " ");
            }
            System.out.println();

            if(count1==st_arr.length*5)
                break;
            time++;

        }
    }
}