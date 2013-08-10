package blake.Digest;

import java.util.Arrays;
import java.util.List;

import blake.structures.Block512;
import blake.structures.Suport;

public class Blake256  {

	int NUMBER_OF_ROUNDS=14;
	int dataLenght;
	protected int[] h = new int[8];
	protected int[] v = new int[16];
	protected int[] s = {0,0,0,0};
	protected int[] t = {0,0};
	
	public Blake256() {
	}

	protected void update(Block512 b) {
		int[] block=b.toIntArr();
		t[0] += (b.dataBytes*8);
		updateVStates();
		//System.out.println(Suport.toHex(v)); //////////////////////////////////////
		//System.out.println(Suport.toHex(t)); //////////////////////////////////////
		for(int i = 0;i<NUMBER_OF_ROUNDS;i++){
			doRound(i,block);
			//System.out.println(Suport.toHex(v)); //////////////////////////////////////
			//System.out.println(Suport.toHex(t)); //////////////////////////////////////
		}
		finalStep();
	}
	
	private void doRound(int r, int[] m) {
		G(0,r,m);
        G(1,r,m);
        G(2,r,m);
        G(3,r,m);
        G(4,r,m);
        G(5,r,m);
        G(6,r,m);
        G(7,r,m);
	}
	
	private void finalStep() {
        h[0] = h[0] ^ s[0] ^ v[0] ^ v[8];
        h[1] = h[1] ^ s[1] ^ v[1] ^ v[9];
        h[2] = h[2] ^ s[2] ^ v[2] ^ v[10];
        h[3] = h[3] ^ s[3] ^ v[3] ^ v[11];
        h[4] = h[4] ^ s[0] ^ v[4] ^ v[12];
        h[5] = h[5] ^ s[1] ^ v[5] ^ v[13];
        h[6] = h[6] ^ s[2] ^ v[6] ^ v[14];
        h[7] = h[7] ^ s[3] ^ v[7] ^ v[15];
	}

	private void G(int i,int r, int[] m){
        int A = Gindex[i][0];
        int B = Gindex[i][1];
        int C = Gindex[i][2];
        int D = Gindex[i][3];
        
        v[A] = v[A] + v[B] + ( m[perm[r][2*i]] ^ c[perm[r][(2*i)+1]]);
        v[D] = java.lang.Integer.rotateRight((v[D] ^ v[A]),16);
        v[C] = v[C] + v[D];
        v[B] = java.lang.Integer.rotateRight((v[B] ^ v[C]),12);
        v[A] = v[A] + v[B] + ( m[perm[r][(2*i)+1]] ^ c[perm[r][2*i]]);
        v[D] = java.lang.Integer.rotateRight((v[D] ^ v[A]),8);
        v[C] = v[C] + v[D];
        v[B] = java.lang.Integer.rotateRight((v[B] ^ v[C]),7);
    }

	protected void initialize() {
		h=Arrays.copyOf(initialvalue,8);
	}

	private void updateVStates() {
		v[0] = h[0];
		v[1] = h[1];
		v[2] = h[2];
		v[3] = h[3];
		v[4] = h[4];
		v[5] = h[5];
		v[6] = h[6];
		v[7] = h[7];
		v[8] = s[0]^c[0];
		v[9] = s[1]^c[1];
		v[10] = s[2]^c[2];
		v[11] = s[3]^c[3];
		v[12] = t[0]^c[4];
		v[13] = t[0]^c[5];
		v[14] = t[1]^c[6];
		v[15] = t[1]^c[7];
		
	}

	private static final int perm[][] = 
	    {
	        {0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15},
	        {14, 10,  4,  8,  9, 15, 13,  6,  1, 12,  0,  2, 11,  7,  5,  3},
	        {11,  8 ,12 , 0  ,5 , 2 ,15 ,13, 10 ,14 , 3 , 6 , 7 , 1 , 9 , 4},
	        {7,  9 , 3 , 1 ,13 ,12 ,11 ,14 , 2 , 6 , 5 ,10 , 4 , 0 ,15 , 8},
	        {9 , 0 , 5 , 7 , 2 , 4 ,10, 15 ,14 , 1 ,11 ,12 , 6 , 8 , 3 ,13},
	        {2, 12 , 6 ,10 , 0 ,11 , 8 , 3 , 4, 13 , 7 , 5 ,15, 14,  1 , 9},
	        {12,  5 , 1, 15, 14, 13 , 4 ,10 , 0 , 7 , 6 , 3 , 9 , 2 , 8 ,11},
	        {13, 11 , 7 ,14, 12,  1 , 3 , 9 , 5 , 0, 15,  4 , 8 , 6 , 2 ,10},
	        {6 ,15 ,14  ,9, 11 , 3 , 0 , 8, 12 , 2 ,13 , 7 , 1 , 4, 10 , 5},
	        {10 , 2 , 8 , 4 , 7 , 6 , 1 , 5 ,15, 11 , 9 ,14  ,3, 12, 13 , 0},
	        {0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15},
	        {14, 10,  4,  8,  9, 15, 13,  6,  1, 12,  0,  2, 11,  7,  5,  3},
	        {11,  8 ,12 , 0  ,5 , 2 ,15 ,13, 10 ,14 , 3 , 6 , 7 , 1 , 9 , 4},
	        {7,  9 , 3 , 1 ,13 ,12 ,11 ,14 , 2 , 6 , 5 ,10 , 4 , 0 ,15 , 8},
	        {9 , 0 , 5 , 7 , 2 , 4 ,10, 15 ,14 , 1 ,11 ,12 , 6 , 8 , 3 ,13},
	        {2, 12 , 6 ,10 , 0 ,11 , 8 , 3 , 4, 13 , 7 , 5 ,15, 14,  1 , 9}
	    };
	
	private static final int[][] Gindex = {
        {0,4,8,12},
        {1,5,9,13},
        {2,6,10,14},
        {3,7,11,15},
        {0,5,10,15},
        {1,6,11,12},
        {2,7,8,13},
        {3,4,9,14}
    };
	
    protected int c[] = {
        0x243F6A88,
        0x85A308D3,
        0x13198A2E,
        0x03707344,
        0xA4093822,
        0x299F31D0,
        0x082EFA98,
        0xEC4E6C89,
        0x452821E6,
        0x38D01377, 
        0xBE5466CF,
        0x34E90C6C,
        0xC0AC29B7,
        0xC97C50DD, 
        0x3F84D5B5,
        0xB5470917
    };
    
	protected static final int initialvalue[] = {
        0x6A09E667,
        0xBB67AE85,
        0x3C6EF372,
        0xA54FF53A,
        0x510E527F,
        0x9B05688C,
        0x1F83D9AB,
        0x5BE0CD19
    };
	
	public String digest(byte[] data){
		initialize();
		List<Block512> blocks = Block512.fromByteArray(data);
		for(Block512 b : blocks){
			//int[] intarr = b.toIntArr();
			//System.out.println(Suport.toHex(intarr)); ///////////////////////////
			//System.out.println(Suport.toHex(s));   	///////////////////////////
			//System.out.println(Suport.toHex(t));   	///////////////////////////
			update(b);
		}
		return Suport.toHex(Suport.intAr2byteAr(h));
	}

}
