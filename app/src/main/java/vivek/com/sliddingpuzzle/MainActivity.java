/*
 * Copyright (c) 2016. Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *                            The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *                            THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package vivek.com.sliddingpuzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;

import vivek.com.sliddingpuzzle.model.Position;
import vivek.com.sliddingpuzzle.model.TileItem;
import vivek.com.sliddingpuzzle.utils.BitmapSplitter;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    RelativeLayout fullBoardView;
    Bitmap[][] bitmapTiles;
    TileItem[][] puzzleTiles;
    private static int boardWidth = 450;
    private static int numberOfRows = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullBoardView = (RelativeLayout) findViewById(R.id.puzzleFullBoardView);
        bitmapTiles = this.createTileBitmaps();
        puzzleTiles = this.initializePuzzleTiles(bitmapTiles);
        this.renderTiles(puzzleTiles);
    }

    private Bitmap[][] createTileBitmaps() {
        Bitmap image = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.puzzle1);
        return BitmapSplitter.split(image, boardWidth, numberOfRows);
    }

    private ArrayList<Bitmap> shuffleTiles(Bitmap[][] bitmapTiles) {

        ArrayList<Bitmap> bitmapList = new ArrayList<>();

        for (int i = 0; i < numberOfRows; i++) {
            for(int j = 0; j< numberOfRows; j++) {
                bitmapList.add(bitmapTiles[i][j]);
            }
        }

        //Remove the list piece
        bitmapList.remove(bitmapList.size()-1);
        bitmapList.add(null);
        Collections.shuffle(bitmapList);
        return bitmapList;
    }

    private TileItem[][] initializePuzzleTiles(Bitmap[][] bitmapTiles) {
        TileItem[][] puzzleTile = new TileItem[numberOfRows][numberOfRows];

        ArrayList<Bitmap> bitmapList = shuffleTiles(bitmapTiles);
        int tileWidth = (boardWidth/numberOfRows);
        int bitmapPosition = 0;

        for (int i = 0; i < numberOfRows; i++) {
            for(int j = 0; j< numberOfRows; j++) {
                TileItem tile = new TileItem(getApplicationContext());
                tile.setId(bitmapPosition);
                tile.setCurrentPosition(new Position(i, j));
                tile.setStartingPosition(new Position(i, j));
                tile.setImage(bitmapList.get(bitmapPosition++));
                tile.setDimension(tileWidth);
                puzzleTile[i][j] = tile;
            }
        }

        return puzzleTile;
    }

    private void renderTiles(TileItem[][] puzzleTiles) {
        for (int i = 0; i < numberOfRows; i++ ) {
            for (int j = 0; j < numberOfRows; j++) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
//                 if (i == 0) {
                     params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//                 } else {
//                     params.tol
//                 }

                params.leftMargin = (j * (boardWidth/numberOfRows))+10;
                params.topMargin = (i * (boardWidth/numberOfRows))+10;

                fullBoardView.addView(puzzleTiles[i][j], params);
                puzzleTiles[i][j].setOnTouchListener(this);

            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        TileItem selectedTile = (TileItem) v;

        return true;
    }
}
