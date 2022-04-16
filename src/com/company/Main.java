package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.System.out;

public class Main {
    public static boolean[][] visited;
    public static void main(String[] args) {
        Random rand = new Random();

        out.print("Enter the size of the matrix : ");
        int size = 4; //need to comment out

        out.println(size);
        Node [][] env = new Node[size][size];
        Node [][] obsv = new Node[size][size];
        visited = new boolean[size][size];
        for (int i=0;i<size ;i++) for (int j = 0; j < size; j++) {
            env[i][j] = new Node();
            obsv[i][j] = new Node();
        }
        int [] w = new int[2];//{rand.nextInt(1),rand.nextInt(3)};
        while(true){
            w[0]= rand.nextInt(size);
            w[1]= rand.nextInt(size);
            if(w[0]>1 && w[1]<=1){
                continue;
            }
            break;
        }
        out.println("The Wumpus is positioned at : ["+w[0]+","+w[1]+"]");
        out.print("Total Number of Pits : ");
        int totalPits=rand.nextInt(1,size);
        out.println(totalPits);

        int [][] pit = new int[totalPits][2];//{{3,2},{1,2},{0,3}};
        for(int i=0;i<totalPits;i++){
            while(true){
                pit[i][0]= rand.nextInt(size);
                pit[i][1]= rand.nextInt(size);
                if(w[0]==pit[i][0] && w[1]==pit[i][1]) continue;
                if(pit[i][0]>1 && pit[i][1]<=1){
                    continue;
                }
                break;
            }
        }
        out.println("Position of pits : "+ Arrays.deepToString(pit));
        out.print("Position of Gold : ");

        setStench(env,w);
        env[w[0]][w[1]].wumpus=1;
        for(int i=0;i<totalPits;i++) {
            setBreeze(env, pit[i]);
            env[pit[i][0]][pit[i][1]].pit=1;
        }
        int[] glit;

        int[][] pos = new int[1 + totalPits][2];
        pos[0]=w;
        int j=1;
        for(int [] i :pit){
            pos[j]=i;
            j++;
        }
        j=0;
        int[][] outpos = new int[size*size-totalPits][2];
        for(int i = 0; i<size; i++){
            for(int k=0;k<size;k++){
                boolean tem=false;
                for(int[] g:pos){
                    if(g[0]==i &&g[1]==k) {
                        tem = true;
                        break;
                    }
                }
                if(tem)continue;
                outpos[j][0]=i;
                outpos[j][1]=k;
                j++;
            }
        }
        glit = outpos[rand.nextInt(outpos.length)];
        out.println(Arrays.toString(glit));
        env[glit[0]][glit[1]].data[2]=1;
        env[glit[0]][glit[1]].gold=1;

        ANode pri = new ANode(env);
        pri.pri();
        AgentMoment_T(obsv,env);
    }

    private static void AgentMoment_T(Node[][] obsv, Node[][] env) {
        int size = env.length;
        int[] startPos = new int[]{size -1,0};
        int t;

        t=AgentMoment(startPos,obsv,env);
        for(int i=0;i< visited.length;i++){
            for(int j=0; j<visited.length; j++){
                visited[i][j]=false;
            }
        }
        if(t<=0) {
            out.println();
            t=AgentMoment(startPos, obsv, env);
        }
        for(int i=0;i< visited.length;i++){
            for(int j=0; j<visited.length; j++){
                visited[i][j]=false;
            }
        }
        if(t<=0){
            out.println();
            t=AgentMoment_Z(startPos, obsv, env);
        }
        out.println();
        out.println("The value of t is : "+t);
        if(t!=1) {
            out.println();
            out.println("The Agent could not determine a path");
        }
        ANode pri = new ANode(obsv);
        pri.pri();
    }

    private static int AgentMoment_Z(int[] pos, Node[][] obs, Node[][] env) {
        int rows = env.length;
        int cols = env.length;
        int t = 0;
        if(visited[pos[0]][pos[1]]){
            return 0;
        }
        out.print("->["+pos[0]+" "+pos[1]+"]");
        visited[pos[0]][pos[1]]=true;
        if(pos[0]+1<rows && obs[pos[0]+1][pos[1]].wumpus==0 && obs[pos[0]+1][pos[1]].pit==0)t=AgentMoment_Z(new int[]{pos[0]+1,pos[1]},obs,env);
        if(t>0)return t;
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        if(pos[0]-1>0 && obs[pos[0]-1][pos[1]].wumpus==0 && obs[pos[0]-1][pos[1]].pit==0)t=AgentMoment_Z(new int[]{pos[0]-1,pos[1]},obs,env);
        if(t>0)return t;
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        if(pos[1]+1<cols && obs[pos[0]][pos[1]+1].wumpus==0 && obs[pos[0]][pos[1]+1].pit==0)t=AgentMoment_Z(new int[]{pos[0],pos[1]+1},obs,env);
        if(t>0)return t;
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        if(pos[1]-1>0 && obs[pos[0]][pos[1]-1].wumpus==0 && obs[pos[0]][pos[1]-1].pit==0)t=AgentMoment_Z(new int[]{pos[0],pos[1]-1},obs,env);
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        return t;
    }

    private static int AgentMoment(int[] startPos, Node[][] obsv, Node[][] env) {
        int rows = env.length;
        int cols = env.length;
        int t;

        if (startPos[0]>=rows || startPos[0]<0 || startPos[1]>=cols || startPos[1]<0 || visited[startPos[0]][startPos[1]])
            return -1;
        visited[startPos[0]][startPos[1]]=true;
        List<Boolean> inference = infer(obsv,env,startPos);
        out.print("->["+startPos[0]+" "+startPos[1]+"]");

        if(inference.get(1)){
            out.println();
            out.println("GOLD IS FOUND");
            return 1;
        }
        else if(inference.get(3)) return 2;
        if(inference.get(0)){
            t=AgentMoment(new int[]{startPos[0]+1,startPos[1]},obsv,env);
//            if(t==0)out.print("->["+startPos[0]+" "+startPos[1]+"]");
            if(t>0)return t;
            t=AgentMoment(new int[]{startPos[0]-1,startPos[1]},obsv,env);
//            if(t==0)out.print("->["+startPos[0]+" "+startPos[1]+"]"+(startPos[0]-1)+" "+startPos[1]);
            if(t>0)return t;
            t=AgentMoment(new int[]{startPos[0],startPos[1]-1},obsv,env);
//            if(t==0)out.print("->["+startPos[0]+" "+startPos[1]+"]");
            if(t>0)return t;
            t=AgentMoment(new int[]{startPos[0],startPos[1]+1},obsv,env);
//            if(t==0)out.print("->["+startPos[0]+" "+startPos[1]+"]");
            if(t>0)return t;
        }
        else if(inference.get(2)){
            t=MomentZ(obsv,startPos,env);
        }
        else{
            if(obsv[startPos[0]][startPos[1]].data[0]==1 && obsv[startPos[0]][startPos[1]].data[1]==1) return 0;
            t=lookAhead(startPos,obsv,env);
        }
//        ANode pri = new ANode(obsv);
//        out.println(Arrays.toString(startPos));
//        pri.pri();
        return t;
    }

    private static int MomentZ(Node[][] obs, int[] pos, Node[][] env) {
        int rows = obs.length;
        int cols = obs.length;
        int t=0;
        if(pos[0]+1<rows && obs[pos[0]+1][pos[1]].wumpus!=1 && obs[pos[0]+1][pos[1]].pit!=1)t=AgentMoment(new int[]{pos[0]+1,pos[1]},obs,env);
        if(t>0)return t;
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        if(pos[0]-1>0 && obs[pos[0]-1][pos[1]].wumpus!=1 && obs[pos[0]-1][pos[1]].pit!=1)t=AgentMoment(new int[]{pos[0]-1,pos[1]},obs,env);
        if(t>0)return t;
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        if(pos[1]+1<cols && obs[pos[0]][pos[1]+1].wumpus!=1 && obs[pos[0]][pos[1]+1].pit!=1)t=AgentMoment(new int[]{pos[0],pos[1]+1},obs,env);
        if(t>0)return t;
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        if(pos[1]-1>0 && obs[pos[0]][pos[1]-1].wumpus!=1 && obs[pos[0]][pos[1]-1].pit!=1)t=AgentMoment(new int[]{pos[0],pos[1]-1},obs,env);
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        return t;
    }

    private static int lookAhead(int[] pos, Node[][] obs, Node[][] env) {

        int rows = obs.length;
        int cols = obs.length;
        int t=0;

        if(pos[0]+1<rows && obs[pos[0]+1][pos[1]].wumpus==-1 && obs[pos[0]+1][pos[1]].pit==-1)t=AgentMoment(new int[]{pos[0]+1,pos[1]},obs,env);
        if(t>0)return t;
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        if(pos[0]-1>=0 && obs[pos[0]-1][pos[1]].wumpus==-1 && obs[pos[0]-1][pos[1]].pit==-1)t=AgentMoment(new int[]{pos[0]-1,pos[1]},obs,env);
        if(t>0)return t;
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        if(pos[1]+1<cols && obs[pos[0]][pos[1]+1].wumpus==-1 && obs[pos[0]][pos[1]+1].pit==-1)t=AgentMoment(new int[]{pos[0],pos[1]+1},obs,env);
        if(t>0)return t;
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        if(pos[1]-1>=0 && obs[pos[0]][pos[1]-1].wumpus==-1 && obs[pos[0]][pos[1]-1].pit==-1)t=AgentMoment(new int[]{pos[0],pos[1]-1},obs,env);
//        if(t==0)out.print("->["+pos[0]+" "+pos[1]+"]");
        return t;
    }

    private static List<Boolean> infer(Node[][] obs, Node[][] env, int[] pos) {
        obs[pos[0]][pos[1]]=env[pos[0]][pos[1]];
        List<Boolean> inf = new ArrayList<>();
        inf.add(false);inf.add(false);inf.add(false);inf.add(false);
        boolean tp=false;
        boolean tw=false;
        //inf--->[safe, gold,checkPit,Death]
        if (env[pos[0]][pos[1]].wumpus==1 || env[pos[0]][pos[1]].pit==1){
            out.println();
            out.println("AGENT DIED");
            inf.set(3,true);
            return inf;
        }

        if (env[pos[0]][pos[1]].data[0]==0 && env[pos[0]][pos[1]].data[1]==0){
            inf.set(0,true);
            obs[pos[0]][pos[1]].safe=1;
        }
        else {
            if(env[pos[0]][pos[1]].data[1]==1){
                if(!checkPit(obs,pos)) {
                    tp = setPseudoPit(obs, pos);
                }
                else{
                    inf.set(2,true);
                }
            }
            if(env[pos[0]][pos[1]].data[0]==1){
                if(!checkWumpus(obs, pos)) {
                    tw = setPseudoWumpus(obs, pos);
                }
                else{
                    inf.set(2,true);
                }
            }
        }
        if (tp || tw){
            inf.set(2,true);
        }
        if(env[pos[0]][pos[1]].gold==1) inf.set(1,true);
        return inf;
    }

    private static boolean checkWumpus(Node[][] env, int[] pos) {
        int i= pos[0];
        int j= pos[1];
        if(i==0){
            if(env[i+1][j].wumpus==1) return true;
        }
        else if(i==env.length-1){
            if(env[i-1][j].wumpus==1) return true;
        }
        else{
            if(env[i+1][j].wumpus==1 || env[i-1][j].wumpus==1) return true;
        }
        if(j==0) {
            if (env[i][j + 1].wumpus == 1) return true;
        }
        else if(j==env.length-1){
            if(env[i][j-1].wumpus==1) return true;
        }
        else{
            if (env[i][j+1].wumpus == 1 || env[i][j-1].wumpus==1) return true;
        }
        return false;

    }

    private static boolean checkPit(Node[][] env, int[] pos) {
        int i= pos[0];
        int j= pos[1];
        if(i==0){
            if(env[i+1][j].pit==1) return true;
        }
        else if(i==env.length-1){
            if(env[i-1][j].pit==1) return true;
        }
        else{
            if(env[i+1][j].pit==1 || env[i-1][j].pit==1) return true;
        }
        if(j==0) {
            if (env[i][j + 1].pit == 1) return true;
        }
        else if(j==env.length-1){
            if(env[i][j-1].pit==1) return true;
        }
        else{
            if (env[i][j+1].pit == 1 || env[i][j-1].pit==1) return true;
        }
        return false;
    }

    private static boolean setPseudoWumpus(Node[][] env, int[] w) {
        int i= w[0];
        int j= w[1];
        boolean tw = false;

        if(i==0 && !visited[i+1][j]){
            if(env[i+1][j].wumpus==-1 ){
                env[i+1][j].wumpus=1;
                tw =true;
            }
            else env[i+1][j].wumpus=-1;
        }
        else if(i==env.length-1 && !visited[i-1][j]){
            if(env[i-1][j].wumpus==-1) {
                env[i - 1][j].wumpus = 1;
                tw =true;
            }
            else env[i-1][j].wumpus=-1;
        }
        else{

            if(env[i+1][j].wumpus==-1 && !visited[i+1][j]){
                env[i+1][j].wumpus=1;
                tw =true;
            }
            else env[i + 1][j].wumpus = -1;
            if(env[i-1][j].wumpus==-1&& !visited[i-1][j]) {
                env[i - 1][j].wumpus = 1;
                tw =true;
            }
            else env[i-1][j].wumpus=-1;
        }
        if(j==0){
            if(env[i][j+1].wumpus==-1 && !visited[i][j+1]) {
                env[i][j+1].wumpus=1;
                tw =true;
            }
            else env[i][j+1].wumpus=-1;
        }
        else if(j==env.length-1){
            if(env[i][j-1].wumpus==-1 && !visited[i][j-1]){
                env[i][j-1].wumpus=1;
                tw =true;
            }
            else env[i][j-1].wumpus=-1;
        }
        else{
            if(env[i][j+1].wumpus==-1 && !visited[i][j+1]){
                env[i][j+1].wumpus=1;
                tw =true;
            }
            else env[i][j+1].wumpus=-1;

            if(env[i][j-1].wumpus==-1 && !visited[i][j-1]){
                env[i][j-1].wumpus=1;
                tw =true;
            }
            else env[i][j-1].wumpus=-1;
        }
        return tw;
    }

    private static boolean setPseudoPit(Node[][] env, int[] pos) {
        int i= pos[0];
        int j= pos[1];
        boolean tp = false;

        if(i==0){
            if(env[i+1][j].pit==-1 && !visited[i+1][j]){
                env[i+1][j].pit=1;
                tp=true;
            }
            else env[i+1][j].pit=-1;
        }
        else if(i==env.length-1 ){
            if(env[i-1][j].pit==-1 && !visited[i-1][j]) {
                env[i - 1][j].pit = 1;
                tp=true;
            }
            else env[i-1][j].pit=-1;
        }
        else{
            if(env[i+1][j].pit==-1 && !visited[i+1][j]){
                env[i+1][j].pit=1;
                tp=true;
            }
            else env[i + 1][j].pit = -1;
            if(env[i-1][j].pit==-1 && !visited[i-1][j]) {
                env[i - 1][j].pit = 1;
                tp=true;
            }
            else env[i-1][j].pit=-1;
        }
        if(j==0){
            if(env[i][j+1].pit==-1 && !visited[i][j+1]){
                env[i][j+1].pit=1;
                tp=true;
            }
            else env[i][j+1].pit=-1;
        }
        else if(j==env.length-1){
            if(env[i][j-1].pit==-1 && !visited[i][j-1]){
                env[i][j-1].pit=1;
                tp=true;
            }
            else env[i][j-1].pit=-1;
        }
        else{
            if(env[i][j+1].pit==-1 && !visited[i][j+1]){
                env[i][j+1].pit=1;
                tp=true;
            }
            else env[i][j+1].pit=-1;

            if(env[i][j-1].pit==-1 && !visited[i][j-1]){
                env[i][j-1].pit=1;
                tp=true;
            }
            else env[i][j-1].pit=-1;
        }
        return tp;
    }

    private static void setBreeze(Node[][] env, int[] w) {
        int i=w[0];
        int j=w[1];
        if(i==0){
            env[i+1][j].data[1]=1;
        }
        else if(i==env.length-1){
            env[i-1][j].data[1]=1;
        }
        else{
            env[i+1][j].data[1]=1;
            env[i-1][j].data[1]=1;
        }
        if(j==0){
            env[i][j+1].data[1]=1;
        }
        else if(j==env.length-1){
            env[i][j-1].data[1]=1;
        }
        else{
            env[i][j+1].data[1]=1;
            env[i][j-1].data[1]=1;
        }
    }

    private static void setStench(Node[][] env , int[] w) {
        int i=w[0];
        int j=w[1];
        if(i==0){
            env[i+1][j].data[0]=1;
        }
        else if(i==env.length-1){
            env[i-1][j].data[0]=1;
        }
        else{
            env[i+1][j].data[0]=1;
            env[i-1][j].data[0]=1;
        }
        if(j==0){
            env[i][j+1].data[0]=1;
        }
        else if(j==env.length-1){
            env[i][j-1].data[0]=1;
        }
        else{
            env[i][j+1].data[0]=1;
            env[i][j-1].data[0]=1;
        }
    }
}

class Node {
    public int[] data = new int[]{0, 0, 0, 0, 0};//[Stench,Breeze,Glitter,Bump,Scream]
    int pit=0;
    int wumpus=0;
    int gold=0;
    int safe=0;
}

class ANode{
    public Node[][] node;
    public ANode(Node[][] n){
        this.node=n;
    }

    public void pri() {
        out.println("The final Environment is : ");
        for (Node[] nodes : this.node) {
            for (int j = 0; j < this.node.length; j++) {
                out.print("[");
                String temp = "  ";
                if (nodes[j].wumpus == 1) {
                    out.print("Wm");
                } else out.print(temp);
                if (nodes[j].pit == 1) {
                    out.print("Pi");
                } else out.print(temp);
                if (nodes[j].data[1] == 1) {
                    out.print("Br");
                } else out.print(temp);
                if (nodes[j].data[0] == 1) {
                    out.print("St");
                } else out.print(temp);
                if (nodes[j].gold == 1) {
                    out.print("Gd");
                } else out.print(temp);
                out.print("] ");
            }
            out.println();
        }
    }
}