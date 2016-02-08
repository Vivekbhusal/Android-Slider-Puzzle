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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vivek.com.sliddingpuzzle.model.Position;
import vivek.com.sliddingpuzzle.model.TileItem;
import vivek.com.sliddingpuzzle.utils.BitmapSplitter;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    RelativeLayout fullBoardView;
    Bitmap[][] bitmapTiles;
    LinkedHashMap<Integer, TileItem> puzzleItemList;
    LinkedHashMap<Integer, TileItem> shuffledTiles;
    TileItem emptyTile;
    Button ViewOriginalImage;
    ImageView originImage;

    int deviceWidth;

    public static int boardWidth = 600;
    public static int numberOfRows = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceWidth = getWindowManager().getDefaultDisplay().getWidth();

        fullBoardView = (RelativeLayout) findViewById(R.id.puzzleFullBoardView);
        LinearLayout.LayoutParams boardParam = new LinearLayout.LayoutParams(600,600);
        boardParam.leftMargin = (deviceWidth-boardWidth)/2;
        fullBoardView.setLayoutParams(boardParam);

        ViewOriginalImage = (Button) findViewById(R.id.originalImageButton);
        originImage = (ImageView) findViewById(R.id.originalImage);
        ViewOriginalImage.setOnTouchListener(this);

        bitmapTiles = this.createTileBitmaps();
        puzzleItemList = this.initializePuzzleTiles(bitmapTiles);
        this.shuffleAndRenderTiles(puzzleItemList);
    }

    /**
     * Creates the array of bitmap by splitting image into pieces
     * @return
     */
    private Bitmap[][] createTileBitmaps() {
        Bitmap image = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.imagepuzzle2);
        return BitmapSplitter.split(image, boardWidth, numberOfRows);
    }

    /**
     * Shuffle the existing list of tiles to render randomly
     * @param puzzleTile
     * @return
     */
    private LinkedHashMap<Integer, TileItem> shuffleTiles(LinkedHashMap<Integer, TileItem> puzzleTile) {
        //Remove the list piece
        emptyTile = puzzleTile.get(puzzleTile.size()-1);
        emptyTile.setImage(null);
        emptyTile.setIsBlank(true);

        //shuffle
        List keys = new ArrayList(puzzleTile.keySet());
        Collections.shuffle(keys);

        LinkedHashMap<Integer, TileItem> shuffledTile = new LinkedHashMap<>();
        int i=0;
        for (Object o: keys){
            TileItem item = puzzleTile.get(o);
            int xAxis = (i<3)? i : (i%3);
            int yAxis = 0;
            if(i>=3 && i<=5) {
                yAxis = 1;
            } else if(i>=6 && i<=8) {
                yAxis = 2;
            }

            item.setCurrentPosition(new Position(xAxis, yAxis));
            shuffledTile.put((int) o, puzzleTile.get(o));
            i++;
        }
        return shuffledTile;
    }

    /**
     * Creates the array of Tile item, assigns bitmap and create Hashmap
     * @param bitmapTiles
     * @return
     */
    private LinkedHashMap<Integer, TileItem> initializePuzzleTiles(Bitmap[][] bitmapTiles) {
        LinkedHashMap<Integer, TileItem> puzzleItem = new LinkedHashMap<>();
        int tileWidth = (boardWidth/numberOfRows);
        int bitmapPosition = 0;

        for (int i = 0; i < numberOfRows; i++) {
            for(int j = 0; j< numberOfRows; j++) {
                TileItem tile = new TileItem(getApplicationContext());
                tile.setId(bitmapPosition);
                tile.setStartingPosition(new Position(i, j));
                tile.setImage(bitmapTiles[i][j]);
                tile.setDimension(tileWidth);
                tile.setOnTouchListener(this);
                puzzleItem.put(bitmapPosition++, tile);
            }
        }
        return puzzleItem;
    }


    /**
     * Shuffle the origin list of tiles before render.
     *
     * @param puzzleItem
     */
    private void shuffleAndRenderTiles(LinkedHashMap<Integer, TileItem> puzzleItem) {
        shuffledTiles = this.shuffleTiles(puzzleItem);

        for(Map.Entry<Integer, TileItem> entry: shuffledTiles.entrySet()) {
            TileItem item = entry.getValue();
            fullBoardView.addView(item, item.setLayout());
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.originalImageButton) {
            displayOriginalImage(event);
            return true;
        }

        TileItem selectedTile = (TileItem) v;

        //Do nothing if the tile is blank
        if(selectedTile.getIsBlank()) {
            return false;
        }

        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if(!checkIfValidMove(selectedTile))
                    return false;
                break;
            case MotionEvent.ACTION_MOVE:
                dragTilesAround(selectedTile, event);

                break;
            case MotionEvent.ACTION_UP:
                swapTileWithEmpty(selectedTile);
                break;
        }
        return true;
    }

    public boolean checkIfValidMove(TileItem selectedItem) {

        return (selectedItem.isAboveOf(emptyTile)
                || selectedItem.isbelowOf(emptyTile)
                || selectedItem.isLeftOf(emptyTile)
                || selectedItem.isRightOf(emptyTile));
    }

    public void swapTileWithEmpty(TileItem selectedItem) {
        Position selecteItemPosition = selectedItem.getCurrentPosition();

        selectedItem.swapPositionWith(emptyTile.getCurrentPosition());
        emptyTile.swapPositionWith(selecteItemPosition);


    }
    /**
     * Display the original image during button press and hide after release
     * @param event
     */
    public void displayOriginalImage(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                fullBoardView.setVisibility(View.GONE);
                originImage.setVisibility(View.VISIBLE);
                break;
            case MotionEvent.ACTION_UP:
                fullBoardView.setVisibility(View.VISIBLE);
                originImage.setVisibility(View.GONE);
                break;
        }
    }

    public void dragTilesAround(TileItem selectedTile, MotionEvent event) {
        int xCoordinate = (int) event.getRawX();
        int yCoordinate = (int) event.getRawY();

        Log.d("xcoordinate", xCoordinate+"");
        Log.d("ycoordinate", yCoordinate+"");

        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) selectedTile.getLayoutParams();
        param.leftMargin = xCoordinate;
        param.topMargin = yCoordinate;

        selectedTile.setLayoutParams(param);
    }
}
