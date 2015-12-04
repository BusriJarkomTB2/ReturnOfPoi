/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gomokuserver;

import java.util.ArrayList;

/**
 * modified by KomJar
 * @author Nizami
 */
public class Table {

        private static final int EMPTY = -1;
    
	private int width, height;
	private int store[][];

// Membuat matriks untuk permainan gomoku
	Table(int width, int height) {

		this.width = width;
		this.height = height;

		store = new int[width][height];

		for (int i = 0; i < width; i++) {
			for (int z = 0; z < height; z++) {
				store[i][z] = EMPTY;
			}
		}
	}
        
        int lastPlayer;
// Fungsi untuk membuat Move player
	public boolean makeMove(int player, int x, int y) {
		try {
			if (store[x][y] != EMPTY) {
				return false;
			} else {
				store[x][y] = player;
                                lastPlayer = player;
				return true;
			}
		} catch (Exception e) {
			return false;
		}

	}

	//could improve a lot by using last position, currently a bit lazy to do that
        //only sees last player
	public int checkWin() {
		int count, i, z, h, l;

		// Check North to South
		for (z = 0; z < width; z++) {
			count = 0;
			for (i = 0; i < height; i++) {
                                if (store[z][i] == lastPlayer) {
                                        count += 1;
                                } else {
                                        count = 0;
                                }
				if (count >= 5) {
					return lastPlayer;
				}
			}
		}

		// Check West to East
		for (z = 0; z < width; z++) {
			count = 0;
			for (i = 0; i < height; i++) {
                                if (store[i][z] == lastPlayer) {
                                        count += 1;
                                } else {
                                        count = 0;
                                }
				if (count >= 5) {
					return lastPlayer;
				}
			}
		}

		// Check NW to SE
		for (h = 0; h < height - 5; h++) {
			for (l = 0; l < width - 5; l++) {
				count = 0;
				for (z = h, i = l; z <= h + 5 && i <= l + 5; i++, z++) {
					if (store[z][i] == lastPlayer) {
						count++;
					} else {
						count = 0;
					}
					if (count >= 5) {
						return lastPlayer;
					}
				}
			}
		}

		// Check SW to NE
		for (h = 0; h < height - 5; h++) {
			for (l = 0; l < width - 5; l++) {
				count = 0;
				for (z = h, i = l + 5; z <= h + 5 && i >= l - 5; i--, z++) {
					if (store[z][i] == lastPlayer) {
						count++;
					} else {
						count = 0;
					}
					if (count >= 5) {
						return lastPlayer;
					}
				}
			}
		}
		return EMPTY;
	}
        
        //could improve a lot by using last position, currently a bit lazy to do that
        //only sees last player
	public int[][] getWinPieces() {
		int i, z, h, l;
                
                ArrayList<int[]> winPieces;

		// Check North to South
		for (z = 0; z < width; z++) {
                        winPieces = new ArrayList<>();
			for (i = 0; i < height; i++) {
                            if (store[z][i] == lastPlayer) {
                                int[] a = new int[2];
                                a[0]=z;
                                a[1]=i;
                                winPieces.add(a);
                            } else {
                                    winPieces = new ArrayList<>();
                            }
                            if (winPieces.size() >= 5) {
                                    return winPieces.toArray(new int[winPieces.size()][]);
                            }
			}
		}

		// Check West to East
		for (z = 0; z < width; z++) {
			winPieces = new ArrayList<>();
			for (i = 0; i < height; i++) {
                            if (store[i][z] == lastPlayer) {
                                int[] a = new int[2];
                                a[0]=i;
                                a[1]=z;
                                winPieces.add(a);
                            } else {
                                    winPieces = new ArrayList<>();
                            }
                            if (winPieces.size() >= 5) {
                                    return winPieces.toArray(new int[winPieces.size()][]);
                            }
			}
		}

		// Check NW to SE
		for (h = 0; h < height - 5; h++) {
			for (l = 0; l < width - 5; l++) {
				winPieces = new ArrayList<>();
				for (z = h, i = l; z <= h + 5 && i <= l + 5; i++, z++) {
                                    if (store[z][i] == lastPlayer) {
                                        int[] a = new int[2];
                                        a[0]=z;
                                        a[1]=i;
                                        winPieces.add(a);
                                    } else {
                                            winPieces = new ArrayList<>();
                                    }
                                    if (winPieces.size() >= 5) {
                                        return winPieces.toArray(new int[winPieces.size()][]);
                                    }
				}
			}
		}

		// Check SW to NE
		for (h = 0; h < height - 5; h++) {
			for (l = 0; l < width - 5; l++) {
				winPieces = new ArrayList<>();
				for (z = h, i = l + 5; z <= h + 5 && i >= l - 5; i--, z++) {
                                    if (store[z][i] == lastPlayer) {
                                        int[] a = new int[2];
                                        a[0]=z;
                                        a[1]=i;
                                        winPieces.add(a);
                                    } else {
                                            winPieces = new ArrayList<>();
                                    }
                                    if (winPieces.size() >= 5) {
                                        return winPieces.toArray(new int[winPieces.size()][]);
                                    }
				}
			}
		}
		return null;
	}

	public int[][] getStore() {
		return store;
	}

	public void printTable() {
		int i, z;

		// First Line

		for (i = 0; i < width + 2; i++) {
			if (i > 0 && i < 20)
				System.out.printf(" %d ", (i - 1) % 10);
			else
				System.out.printf(" # ");
		}
		System.out.printf("\n");

		for (i = 0; i < height; i++) {
			for (z = 0; z < width; z++) {
				if (z == 0) {
					System.out.printf(" %d ", i % 10);
				}
				if (store[z][i] == 0) {
					System.out.printf(" _ ");
				} else {
					System.out.printf(" %d ", store[z][i]);
				}

				if (z == width - 1) {
					System.out.printf(" %d\n", i % 10);
				}
			}
		}

		for (i = 0; i < width + 2; i++) {
			if (i > 0 && i < 20)
				System.out.printf(" %d ", (i - 1) % 10);
			else
				System.out.printf(" # ");
		}
		System.out.printf("\n");

	}
}