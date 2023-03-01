import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Board extends JPanel {

    private final int NUM_IMAGES = 13;      //kullanılan resimler
    private final int CELL_SIZE = 15;       //resimlerin boyutu 15x15

    private final int COVER_FOR_CELL = 10;  //10.png
    private final int MARK_FOR_CELL = 10;
    private final int EMPTY_CELL = 0;       //0.png
    private final int MINE_CELL = 9;        //9.png
    private final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;       //üstü kapalı mayın bulunan hücre
    private final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;     //kullanıcı tarafından işaretlenimş cell

    private final int DRAW_MINE = 9;    //9.png
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;     //işareti olmayan 12.png

    private final int N_MINES = 40;         //oyundaki mayın sayısı
    private final int N_ROWS = 16;          //satır
    private final int N_COLS = 16;          //sütun

    private final int BOARD_WIDTH = N_COLS * CELL_SIZE + 1;
    private final int BOARD_HEIGHT = N_ROWS * CELL_SIZE + 1;

    private int[] field;        //alan
    private boolean inGame;     //oyunu kazanıp kaybetme durumu için boolean bir ifade
    private int minesLeft;      //işaretleme
    private Image[] img;        //resimler için bir dizi oluşturduk

    private int allCells;       //256 hücre
    private final JLabel statusbar;     //bilgi vermesi için paneldeki yazı

    public Board(JLabel statusbar) {

        this.statusbar = statusbar;
        initBoard();
    }

    private void initBoard() {

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        img = new Image[NUM_IMAGES];//13 indisli bir dizi oluştu

        for (int i = 0; i < NUM_IMAGES; i++) {
            //dizinin içine atar
            var path = "src//resources//" + i + ".png";
            img[i] = (new ImageIcon(path)).getImage();
        }

        addMouseListener(new MinesAdapter());
        newGame();
    }

    private void newGame() {

        int cell;       //hücre

        var random = new Random();
        inGame = true;
        minesLeft = N_MINES;

        allCells = N_ROWS * N_COLS;         //16x16=256 kare
        field = new int[allCells];

        for (int i = 0; i < allCells; i++) {

            field[i] = COVER_FOR_CELL;      //10.png görsel ile tüm karelerin resmini değiştirdi
        }

        statusbar.setText(Integer.toString(minesLeft));     //panelde mayın sayısı ile ilgili sayı göstermesi

        int i = 0;

        while (i < N_MINES) {       //n tane mayın için tekrarlanıyor

            int position = (int) (allCells * random.nextDouble());      //mayınlar random olucak şekilde yüklenecek sayı değerleri belirlendi
            System.out.println("Mayınların yerleri: " + position);
            if ((position < allCells) && (field[position] != COVERED_MINE_CELL)) {

                int current_col = position % N_COLS;        //oluşan mayınların kenarlarını bulmak için
                field[position] = COVERED_MINE_CELL;
                i++;

                /**
                 * if (field[cell] != COVERED_MINE_CELL) {
                 *                         field[cell] += 1;
                 *                     }
                 *                     Hücrelerin her biri sekiz hücreye kadar çevrelenebilir.
                 *                     (Bu, sınır hücreleri için geçerli değildir.)
                 *                     Rastgele yerleştirilmiş mayınların her biri için bitişik hücrelerin sayısını artırıyoruz.
                 *                     Örneğimizde, söz konusu hücrenin üst komşusuna 1 ekliyoruz.
                 */

                if (current_col > 0) {      //Sütunda tarama yapıyor    1.sütun
                    cell = position - 1 - N_COLS;
                    //23 = 40       - 1 - 16
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position - 1;
                    //39 = 40       - 1
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }

                    cell = position + N_COLS - 1;
                    //55 = 40       + 16     - 1
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }

                cell = position - N_COLS;   //sol kenar 2.sütun
                //24 = 40       - 16
                if (cell >= 0) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                cell = position + N_COLS;
                //56 = 40       + 16
                if (cell < allCells) {
                    if (field[cell] != COVERED_MINE_CELL) {
                        field[cell] += 1;
                    }
                }

                if (current_col < (N_COLS - 1)) {       //0<15  3.sütun
                    cell = position - N_COLS + 1;
                    //25 = 40       - 16     + 1
                    if (cell >= 0) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + N_COLS + 1;
                    //57 = 40       + 16     + 1
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + 1;
                    //41 = 40       + 1
                    if (cell < allCells) {
                        if (field[cell] != COVERED_MINE_CELL) {
                            field[cell] += 1;
                        }
                    }
                }
            }
        }
    }

    /*
    Boş bir hücreye tıklamak,
    diğer birçok boş hücrenin yanı sıra boş kenarlıklardan oluşan bir alanın etrafında bir
    kenarlık oluşturan bir sayıya sahip hücrelerin ortaya çıkarılmasına yol açar
     */
    private void find_empty_cells(int j) {      //boş hücreleri bulmak için recursive algoritma kul

        int current_col = j % N_COLS;
        int cell;

        if (current_col > 0) {
            cell = j - N_COLS - 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j - 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS - 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

        cell = j - N_COLS;
        if (cell >= 0) {
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    find_empty_cells(cell);
                }
            }
        }

        cell = j + N_COLS;
        if (cell < allCells) {
            if (field[cell] > MINE_CELL) {
                field[cell] -= COVER_FOR_CELL;
                if (field[cell] == EMPTY_CELL) {
                    find_empty_cells(cell);
                }
            }
        }

        if (current_col < (N_COLS - 1)) {
            cell = j - N_COLS + 1;
            if (cell >= 0) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS + 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + 1;
            if (cell < allCells) {
                if (field[cell] > MINE_CELL) {
                    field[cell] -= COVER_FOR_CELL;
                    if (field[cell] == EMPTY_CELL) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {

        int uncover = 0;        //ortaya çıkarmak

        for (int i = 0; i < N_ROWS; i++) {      //satırda

            for (int j = 0; j < N_COLS; j++) {  //sütunda

                int cell = field[(i * N_COLS) + j];

                if (inGame && cell == MINE_CELL) {      //9.png eşit mi? mayın varmı

                    inGame = false;             //mayına tıklamışsındır oyun biter
                }

                if (!inGame) {      //oyun bittiğindeki çizimleri gösteriyor

                    if (cell == COVERED_MINE_CELL) {
                        cell = DRAW_MINE;   //9.png
                    } else if (cell == MARKED_MINE_CELL) {
                        cell = DRAW_MARK;   //11.png
                    } else if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_WRONG_MARK; //12.png
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;  //10.png
                    }

                } else {

                    if (cell > COVERED_MINE_CELL) {
                        cell = DRAW_MARK;       //11.png yap coverla
                    } else if (cell > MINE_CELL) {
                        cell = DRAW_COVER;      //10.png yap coverı sil
                        uncover++;
                    }
                }

                g.drawImage(img[cell], (j * CELL_SIZE),
                        (i * CELL_SIZE), this);
            }
        }

        if (uncover == 0 && inGame) {   //kazandın

            inGame = false;
            statusbar.setText("Game won");

        } else if (!inGame) {           //kaybetme

            statusbar.setText("Game lost");
        }
    }

    private class MinesAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {

            int x = e.getX();       //mouse nin ekrandaki x ve y kordinatları
            int y = e.getY();
            System.out.println("x kordinatı: " + x);
            System.out.println("y kordinatı: " + y);

            int cCol = x / CELL_SIZE;       //tıklanan yerin sütun ve satır kısmını bulur
            int cRow = y / CELL_SIZE;
            System.out.println("cCol (sütun): " + cCol);
            System.out.println("cRow (satır): " + cRow);

            boolean doRepaint = false;

            if (!inGame) {      //oyun bitti ise yeniden başlat

                newGame();
                repaint();
            }

            if ((x < N_COLS * CELL_SIZE) && (y < N_ROWS * CELL_SIZE)) {     //mouse ile 16x16 karelik alanda ise

                if (e.getButton() == MouseEvent.BUTTON3) {// sağ tuş

                    if (field[(cRow * N_COLS) + cCol] > MINE_CELL) {

                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] <= COVERED_MINE_CELL) {//11.png cover işlemi yapıyor ve miesleft azaltıp sol altta gösteriyor

                            if (minesLeft > 0) {
                                field[(cRow * N_COLS) + cCol] += MARK_FOR_CELL;
                                minesLeft--;
                                String msg = Integer.toString(minesLeft);
                                statusbar.setText(msg);
                            } else {
                                statusbar.setText("No marks left");
                            }
                        } else {//bayrakları silme

                            field[(cRow * N_COLS) + cCol] -= MARK_FOR_CELL;
                            minesLeft++;
                            String msg = Integer.toString(minesLeft);
                            statusbar.setText(msg);
                        }
                    }

                } else {    //sol tuş

                    if (field[(cRow * N_COLS) + cCol] > COVERED_MINE_CELL) {

                        return;
                    }

                    if ((field[(cRow * N_COLS) + cCol] > MINE_CELL)
                            && (field[(cRow * N_COLS) + cCol] < MARKED_MINE_CELL)) {

                        field[(cRow * N_COLS) + cCol] -= COVER_FOR_CELL;    //cover kaldırıyoruz
                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] == MINE_CELL) {// mayın ise oyun biter
                            inGame = false;
                        }

                        if (field[(cRow * N_COLS) + cCol] == EMPTY_CELL) {
                            find_empty_cells((cRow * N_COLS) + cCol);       //recursive empty metotuna gönder

                        }
                    }
                }

                if (doRepaint) {    //true oldukça tekrarla
                    repaint();
                }
            }
        }
    }
}